FROM openjdk:8-jre
LABEL maintainer="colin@marketcetera.com"

WORKDIR /opt/Marketcetera

# RUN apt-get update && apt-get install -y telnet
# RUN apt-get update && apt-get install -y procps

# Create the marketcetera user
RUN useradd marketcetera
USER marketcetera

# Copy the latest build artifacts to the Marketcetera home directory
COPY --chown=marketcetera:marketcetera src/main/sample_data/bin/dare.sh /opt/Marketcetera/dare/bin/
COPY --chown=marketcetera:marketcetera src/main/sample_data/bin/setEnv.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/contrib/docker_start.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/contrib/env.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/bin/startServerComponents.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/bin/stopServerComponents.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/conf /opt/Marketcetera/dare/conf/
COPY --chown=marketcetera:marketcetera src/main/sample_data/contrib /opt/Marketcetera/dare/contrib/
COPY --chown=marketcetera:marketcetera target/output/* /opt/Marketcetera/dare/lib/
COPY --chown=marketcetera:marketcetera target/docker-package-4.1.0-SNAPSHOT.jar /opt/Marketcetera/dare/lib/
RUN chmod ug+x /opt/Marketcetera/*.sh
RUN chmod ug+x /opt/Marketcetera/dare/bin/*.sh
RUN mkdir -p /opt/Marketcetera/dare/logs
RUN touch /opt/Marketcetera/dare/logs/dare_gc.out

EXPOSE 8080 9010

ENTRYPOINT ["sh", "-c"]
CMD [ "/opt/Marketcetera/docker_start.sh" ]
