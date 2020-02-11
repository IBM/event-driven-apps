FROM node:8-stretch

# Change working directory
WORKDIR "/app"

# Update packages and install dependency packages for services
RUN apt-get update \
 && dpkg -P libssl-dev \
 && apt-get install -y liblz4-dev libssl1.0-dev libsasl2-dev \
 && apt-get clean \
 && export LDFLAGS=-L/usr/local/opt/openssl/lib \
 && export CPPFLAGS=-I/usr/local/opt/openssl/include \
 && echo 'Finished installing dependencies'

# Install npm production packages
COPY package.json /app/
RUN cd /app && npm install --production

COPY . /app

ENV NODE_ENV production
ENV PORT 3000

EXPOSE 3000

CMD ["npm", "start"]
