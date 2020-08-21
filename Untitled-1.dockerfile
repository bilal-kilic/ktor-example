FROM adoptopenjdk/openjdk14-openj9
RUN mkdir -p /assets
ADD /Users/bilal.kilic/workspace/personal/ktor-test/assets/newrelic-agent.jar root/assets/