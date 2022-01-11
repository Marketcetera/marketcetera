FROM openjdk:11-jre
LABEL maintainer="colin@marketcetera.com"

WORKDIR /opt/Marketcetera

# Create the marketcetera user
RUN useradd marketcetera
USER marketcetera

# Copy the latest build artifacts to the Marketcetera home directory
COPY --chown=marketcetera:marketcetera src/main/sample_data/dare/bin/dare.sh /opt/Marketcetera/dare/bin/
COPY --chown=marketcetera:marketcetera src/main/sample_data/ui/bin/ui.sh /opt/Marketcetera/ui/bin/
COPY --chown=marketcetera:marketcetera src/main/sample_data/ui/conf /opt/Marketcetera/ui/conf/
COPY --chown=marketcetera:marketcetera src/main/sample_data/dare/bin/setEnv.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/dare/contrib/docker_start.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/dare/contrib/env.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/dare/bin/startServerComponents.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/dare/bin/stopServerComponents.sh /opt/Marketcetera/
COPY --chown=marketcetera:marketcetera src/main/sample_data/dare/conf /opt/Marketcetera/dare/conf/
COPY --chown=marketcetera:marketcetera src/main/sample_data/dare/contrib /opt/Marketcetera/dare/contrib/
COPY --chown=marketcetera:marketcetera target/output-dare/* /opt/Marketcetera/dare/lib/
#COPY --chown=marketcetera:marketcetera target/output-webui/* /opt/Marketcetera/ui/lib/
#COPY --chown=marketcetera:marketcetera target/output-common/* /opt/Marketcetera/common/lib/
RUN chmod ug+x /opt/Marketcetera/*.sh
RUN chmod ug+x /opt/Marketcetera/dare/bin/*.sh
#RUN chmod ug+x /opt/Marketcetera/ui/bin/*.sh
RUN mkdir -p /opt/Marketcetera/dare/logs
#RUN mkdir -p /opt/Marketcetera/ui/logs
RUN touch /opt/Marketcetera/dare/logs/dare_gc.out

EXPOSE 8080 9000 9010 9020

ENTRYPOINT ["sh", "-c"]
CMD [ "/opt/Marketcetera/docker_start.sh" ]
