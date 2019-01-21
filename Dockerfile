FROM debian:buster
ARG UID
ARG POKY_REV

USER root

ENV USER deby
ENV PASS deby
ENV POKY_GIT_URL https://git.yoctoproject.org/git/poky

RUN bash -c 'if test -n "$http_proxy"; then echo "Acquire::http::proxy \"$http_proxy\";" > /etc/apt/apt.conf.d/99proxy; fi'
RUN apt-get update
RUN apt-get install -y --no-install-recommends \
	gawk wget git-core diffstat unzip texinfo gcc-multilib \
	build-essential chrpath socat cpio python python3 python3-pip \
	python3-pexpect xz-utils debianutils iputils-ping file locales

RUN sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen && locale-gen
RUN useradd -d /home/$USER -U -m -s /bin/bash -u $UID $USER

USER $USER

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

RUN bash -c 'if test -n "$http_proxy"; then git config --global http.proxy "$http_proxy"; fi'
RUN bash -c 'if test -n "$https_proxy"; then git config --global https.proxy "$https_proxy"; fi'
RUN bash -c 'if test -n "$no_proxy"; then git config --global core.noproxy "$no_proxy"; fi'

RUN git clone $POKY_GIT_URL /home/$USER/poky
RUN cd /home/$USER/poky && git checkout $POKY_REV
RUN mkdir -p /home/$USER/poky/meta-debian
RUN mkdir -p /home/$USER/build/downloads
