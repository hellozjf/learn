FROM python:3.7.3-slim
MAINTAINER hellozjf
RUN pip install tensorflow flask tqdm
EXPOSE 5000

WORKDIR /idmask
ADD input_ids_mask_segment_label.py /idmask/input_ids_mask_segment_label.py
ADD tokenization.py /idmask/tokenization.py
ADD vocab.txt /idmask/vocab.txt

ENTRYPOINT ["python", "input_ids_mask_segment_label.py"]
