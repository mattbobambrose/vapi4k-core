VERSION=1.0.0

default: versioncheck

build-all: clean stage

stop:
	./gradlew --stop

clean:
	./gradlew clean

compile: build

build:
	./gradlew build -x test

cont-build:
	./gradlew -t build -x test

tests:
	./gradlew --rerun-tasks check

jar:
	./gradlew uberJar

dist:
	./gradlew installDist

stage:
	./gradlew stage

versioncheck:
	./gradlew dependencyUpdates

buildconfig:
	./gradlew generateBuildConfig

kdocs:
	./gradlew dokkaHtml

publish:
	./gradlew publishToMavenLocal

upgrade-wrapper:
	./gradlew wrapper --gradle-version=8.9 --distribution-type=bin
