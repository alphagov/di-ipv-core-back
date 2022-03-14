FROM --platform=linux/arm64/v8 amazonlinux:2

RUN set -ex \
    && yum install -y gcc glibc-devel zlib-devel tar gzip unzip libstdc++-static

ENV JAVA_HOME="/opt/graalvm" \
    GRAALVM_HOME="/opt/graalvm" \
    GRAALVM_VERSION=22.0.0.2 \
    GRAALVM_DOWNLOAD_SHA256="1cc0263d95f642dada4e290dca7f49c0456cefa7b690b67e3e5c159b537b2c58"

RUN set -ex \
    # Install GraalVM - https://github.com/graalvm/graalvm-ce-builds/releases
    && mkdir -p $JAVA_HOME \
    && curl -LSso /var/tmp/graalvm-ce-java11-linux-aarch64-$GRAALVM_VERSION.tar.gz https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-$GRAALVM_VERSION/graalvm-ce-java11-linux-aarch64-$GRAALVM_VERSION.tar.gz \
    && echo "$GRAALVM_DOWNLOAD_SHA256 /var/tmp/graalvm-ce-java11-linux-aarch64-$GRAALVM_VERSION.tar.gz" | sha256sum -c - \
    && tar xzvf /var/tmp/graalvm-ce-java11-linux-aarch64-$GRAALVM_VERSION.tar.gz -C $JAVA_HOME --strip-components=1 \
    && rm /var/tmp/graalvm-ce-java11-linux-aarch64-$GRAALVM_VERSION.tar.gz \
    && for tool_path in $JAVA_HOME/bin/*; do \
          tool=`basename $tool_path`; \
          update-alternatives --install /usr/bin/$tool $tool $tool_path 10000; \
          update-alternatives --set $tool $tool_path; \
        done

RUN $GRAALVM_HOME/bin/gu install native-image