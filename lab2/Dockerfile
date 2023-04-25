FROM mongo

# Disable authentication for replica sets
RUN echo "security:\n  authorization: disabled" > /etc/mongod.conf

# Install wget and add MongoDB repository for mongo-tools
RUN apt-get update
RUN apt-get install -y wget gnupg
RUN wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | apt-key --keyring /etc/apt/trusted.gpg.d/mongodb-org-4.4.gpg add -
RUN echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu bionic/mongodb-org/4.4 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-4.4.list
RUN apt-get update
RUN apt-get install -y mongodb-database-tools
