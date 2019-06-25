package com.hellozjf.learn.projects.order12306.custom;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author hellozjf
 */
@Slf4j
public class FileCookieStore implements CookieStore, Serializable {

    private static final long serialVersionUID = 8199244246782773730L;

    private TreeSet<Cookie> cookies = null;
    private transient ReadWriteLock lock;
    private File file;

    /**
     * 通过File创建FileCookieStore
     * @param file
     */
    public FileCookieStore(File file) {
        super();
        this.cookies = new TreeSet<>(new CookieIdentityComparator());
        this.lock = new ReentrantReadWriteLock();
        this.file = file;

        syncFromFile();
    }

    /**
     * 通过文件夹名称，文件名称创建FileCookieStore
     * @param folderName
     * @param fileName
     */
    public FileCookieStore(String folderName, String fileName) {
        super();
        File folder = new File(folderName);
        folder.mkdirs();

        this.cookies = new TreeSet<>(new CookieIdentityComparator());
        this.lock = new ReentrantReadWriteLock();
        this.file = new File(folder, fileName);

        syncFromFile();
    }

    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        /* Reinstantiate transient fields. */
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Adds an {@link Cookie HTTP cookie}, replacing any existing equivalent cookies.
     * If the given cookie has already expired it will not be added, but existing
     * values will still be removed.
     *
     * @param cookie the {@link Cookie cookie} to be added
     * @see #addCookies(Cookie[])
     */
    @Override
    public void addCookie(final Cookie cookie) {
        if (cookie != null) {
            lock.writeLock().lock();
            try {
                // first remove any old cookie that is equivalent
                cookies.remove(cookie);
                if (!cookie.isExpired(new Date())) {
                    cookies.add(cookie);
                }

                syncToFile();
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    /**
     * cookie同步到文件中
     */
    private void syncToFile() {
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
        ) {
            objectOutputStream.writeObject(cookies);
        } catch (Exception e) {
            log.error("e = {}", e);
        }
    }

    /**
     * 从文件中同步cookie
     */
    private void syncFromFile() {
        if (file.exists() && file.length() > 0) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)
            ) {
                TreeSet<Cookie> readCookies = (TreeSet<Cookie>) objectInputStream.readObject();
                if (readCookies != null && readCookies.size() > 0) {
                    this.cookies = readCookies;
                }
            } catch (Exception e) {
                log.error("e = {}", e);
            }
        }
    }

    /**
     * Adds an array of {@link Cookie HTTP cookies}. Cookies are added individually and
     * in the given array order. If any of the given cookies has already expired it will
     * not be added, but existing values will still be removed.
     *
     * @param cookies the {@link Cookie cookies} to be added
     * @see #addCookie(Cookie)
     */
    public void addCookies(final Cookie[] cookies) {
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                this.addCookie(cookie);
            }
        }
    }

    /**
     * Returns an immutable array of {@link Cookie cookies} that this HTTP
     * state currently contains.
     *
     * @return an array of {@link Cookie cookies}.
     */
    @Override
    public List<Cookie> getCookies() {
        lock.readLock().lock();
        try {
            //create defensive copy so it won't be concurrently modified
            return new ArrayList<>(cookies);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取特定名称的Cookie
     *
     * @param name
     * @return
     */
    public Cookie getCookie(String name) {
        lock.readLock().lock();
        try {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(name)) {
                    return cookie;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 移除给定名字的cookie
     * @param name
     * @return
     */
    public Cookie delCookie(String name) {
        lock.writeLock().lock();
        try {
            for (Iterator<Cookie> iterator = cookies.iterator(); iterator.hasNext();) {
                Cookie cookie = iterator.next();
                if (cookie.getName().equalsIgnoreCase(name)) {
                    iterator.remove();
                    return cookie;
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Removes all of {@link Cookie cookies} in this HTTP state
     * that have expired by the specified {@link Date date}.
     *
     * @return true if any cookies were purged.
     * @see Cookie#isExpired(Date)
     */
    @Override
    public boolean clearExpired(final Date date) {
        if (date == null) {
            return false;
        }
        lock.writeLock().lock();
        try {
            boolean removed = false;
            for (final Iterator<Cookie> it = cookies.iterator(); it.hasNext(); ) {
                if (it.next().isExpired(date)) {
                    it.remove();
                    removed = true;
                }
            }
            syncToFile();
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Clears all cookies.
     */
    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            cookies.clear();
            syncToFile();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return cookies.toString();
        } finally {
            lock.readLock().unlock();
        }
    }
}
