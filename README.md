What is meta-debian?
====================

meta-debian is a set of recipes (metadata) for the poky build system, which allows cross-building GNU/Linux images using Debian source packages.
By enabling meta-debian, you can cross-build a small GNU/Linux image with Debian sources for multiple architectures.

The main purpose of meta-debian is to provide reference Linux distribution for embedded systems satisfying the following needs.
* Long-term support
* Stability
* Wide embedded CPU support
* Customizability

Currently, the following software versions are supported in meta-debian.
* Source code: Debian GNU/Linux 8 (jessie)
* Build system: Yocto Project 2.2 (morty)

Quick Start
===========

Install essential packages poky requires into your host system according to http://www.yoctoproject.org/docs/2.2/ref-manual/ref-manual.html#intro-requirements

```sh
$ sudo apt-get install git tar python3
$ sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib build-essential chrpath socat
```

NOTE: The following three packages have version limitation
* git: 1.7.8 or greater
* tar: 1.24 or greater
* python: 2.7.3 or greater not including Python 3.x

Setup repositories.

```sh
$ git clone -b morty git://git.yoctoproject.org/poky.git
$ cd poky
$ git clone -b morty https://github.com/meta-debian/meta-debian.git
$ cd ..
```

Setup build directory.

```sh
$ export TEMPLATECONF=meta-debian/conf
$ source ./poky/oe-init-build-env
```

You can change the target machine by setting `MACHINE` variable in `conf/local.conf` to one of the following machines.
* qemux86 (default)
* qemux86-64
* qemuarm
* qemuarm64
* qemuppc
* qemumips

For example, the target machine is set to QEMU ARM by adding the following difinition to `conf/local.conf`.
```
MACHINE = "qemuarm"
```

Now, the build system is ready.
Build Linux kernel and the minimal rootfs by the following command.
It takes a while to complete (more than 30 minutes).

```sh
$ bitbake core-image-minimal
```

Run the built Linux on QEMU.
Please replace `${MACHINE}` by the target machine you selected in the above step.

```sh
$ runqemu ${MACHINE} nographic
```

Only if `MACHINE` is `qemuarm`, the console should be set to the correct serial device.

```sh
$ runqemu qemuarm nographic bootparams="console=ttyAMA0"
```

After boot, you can login as `root` without password.

If you'd like to reduce the time of bitbake,
please refer to https://github.com/meta-debian/meta-debian-docker.

License
=======

License of meta-debian is same as meta in poky i.e.
All metadata is MIT licensed unless otherwise stated.
Source code included in tree for individual recipes is under the LICENSE stated in the associated recipe (.bb file) unless otherwise stated.

See COPYING.MIT for more details about MIT license.

Community Resources
===================

#### Project home
* https://github.com/meta-debian/meta-debian

#### Mailing list
* meta-debian@googlegroups.com

#### Mailing list subscription
* meta-debian+subscribe@googlegroups.com
* https://groups.google.com/forum/#!forum/meta-debian/join
