package com.hellozjf.learn.springboot2.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestDocker {

    @Test
    public void test() throws Exception {
        // Create a client based on DOCKER_HOST and DOCKER_CERT_PATH env vars
        final DockerClient docker = DefaultDockerClient.fromEnv().build();

        // Pull an image
        docker.pull("busybox:1.31.1");

        // Bind container ports to host ports
//        final String[] ports = {"80", "22"};
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
//        for (String port : ports) {
//            List<PortBinding> hostPorts = new ArrayList<>();
//            hostPorts.add(PortBinding.of("192.168.2.149", port));
//            portBindings.put(port, hostPorts);
//        }

        // Bind container port 443 to an automatically allocated available host port.
        List<PortBinding> randomPort = new ArrayList<>();
        PortBinding portBinding = PortBinding.of("0.0.0.0", 8443);
        log.debug("port = {}:{}", portBinding.hostIp(), portBinding.hostPort());
        randomPort.add(portBinding);
        portBindings.put("443", randomPort);

        final HostConfig hostConfig = HostConfig.builder()
                .appendBinds("/opt/docker/algorithm-manage-platform:/models")
                .portBindings(portBindings)
                .build();

        // Create container with exposed ports
        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image("busybox")
                .exposedPorts("443")
                .cmd("sh", "-c", "while :; do sleep 1; done")
                .build();

        final ContainerCreation creation = docker.createContainer(containerConfig, "busybox");
        final String id = creation.id();

        // Inspect container
        final ContainerInfo info = docker.inspectContainer(id);

        // Start container
        docker.startContainer(id);

        // Exec command inside running container with attached STDOUT and STDERR
        final String[] command = {"sh", "-c", "ls"};
        final ExecCreation execCreation = docker.execCreate(
                id, command, DockerClient.ExecCreateParam.attachStdout(),
                DockerClient.ExecCreateParam.attachStderr());
        final LogStream output = docker.execStart(execCreation.id());
        final String execOutput = output.readFully();

        // Kill container
        docker.killContainer(id);

        // Remove container
        docker.removeContainer(id);

        // Close the docker client
        docker.close();
    }
}
