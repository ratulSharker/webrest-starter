####################
# Stage: 1 `builder`
####################
FROM azul/zulu-openjdk-alpine:17.0.9 AS builder

RUN apk add maven binutils

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src/
RUN mvn clean package

RUN mv ./target/*.jar ./target/app.jar
WORKDIR /app/target
RUN jar xf app.jar
RUN jdeps --class-path './BOOT-INF/lib/*' -q \
	--recursive \
	--multi-release 17 \
	--ignore-missing-deps \
	--print-module-deps \
	app.jar > jdeps.info

RUN jlink --add-modules $(cat jdeps.info) \
	--strip-debug \
	--compress 2 \
	--no-header-files \
	--no-man-pages \
	--output /custom-jre

####################
# Stage: 2 `runner`
####################
FROM alpine:3.18.4 AS runner
ENV JAVA_HOME /user/java/custom-jre
ENV PATH $JAVA_HOME/bin:$PATH
COPY --from=builder /custom-jre $JAVA_HOME
WORKDIR /app
COPY --from=builder /app/target/app.jar .
EXPOSE 8000
ENTRYPOINT java -jar app.jar