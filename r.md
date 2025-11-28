docker buildx build --platform linux/amd64 -t line .

docker run -p 9000:9000 line