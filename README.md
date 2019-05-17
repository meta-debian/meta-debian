Quick Start
===========

meta-emlinux can be built on a Debian or Ubuntu systems.

Install essential packages for poky:

```sh
$ sudo apt-get install -y gawk wget git-core diffstat unzip texinfo gcc-multilib \
build-essential chrpath socat cpio python python3 python3-pip python3-pexpect \
xz-utils debianutils iputils-ping libsdl1.2-dev xterm
```

Clone meta-emlinux:

```sh
$ mkdir repos
$ git clone -b master https://github.com/miraclelinux/meta-emlinux.git repos/meta-emlinux
```

Setup build directory:

```sh
$ source repos/meta-emlinux/scripts/setup-emlinux build
```

Set your target machine to `conf/local.conf`:

```sh
$ echo "MACHINE = \"qemuarm64\"" >> conf/local.conf
```

Build:

```sh
$ bitbake core-image-minimal
```

License
=======

License of meta-emlinux is same as meta in poky i.e.
All metadata is MIT licensed unless otherwise stated.
Source code included in tree for individual recipes is under the LICENSE stated in the associated recipe (.bb file) unless otherwise stated.

See COPYING.MIT for more details about MIT license.

Community Resources
===================

#### Project home
* https://github.com/miraclelinux/meta-emlinux
