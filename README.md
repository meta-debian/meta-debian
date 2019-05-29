What is meta-debian?
====================

meta-debian is a set of recipes (metadata) for the poky build system, 
which allows cross-building GNU/Linux images using Debian source packages.
By enabling meta-debian, you can cross-build a small GNU/Linux image 
with Debian sources for multiple architectures.

The main purpose of meta-debian is to provide reference Linux distribution 
for embedded systems satisfying the following needs.
* Long-term support
* Stability
* Wide embedded CPU support
* Customizability

Supported Versions
==================

This branch provides recipes to cross-build Debian source packages
of the following Debian version. These recipes are compatible
with the following Yocto Project version.

* Debian GNU/Linux 10 (buster)
* Yocto Project 2.7 (warrior)

Supported Build Environment
===========================

This branch is tested on the following build environment.

* Distribution: Debian GNU/Linux 10 (buster)
* Architecture: amd64

Quick Start
===========

This section introduces how to generate the minimal system with meta-debian
and how to run it on the QEMU environment.

Setup build environment
-----------------------

In case of using the [supported build environment](#supported-build-environment),
run the following commands.

    $ git clone -b warrior git://git.yoctoproject.org/poky.git
    $ git clone -b warrior https://github.com/meta-debian/meta-debian.git poky/meta-debian
    $ sudo ./poky/meta-debian/scripts/install-deps.sh

Otherwise, use the docker container.

    $ git clone -b warrior https://github.com/meta-debian/meta-debian.git
    $ make -C meta-debian/docker

Build target images
-------------------

Setup the build directory.

    $ export TEMPLATECONF=meta-debian/conf
    $ source ./poky/oe-init-build-env

Set `MACHINE` variable in `conf/local.conf` to one of the following machines.

* qemux86 (default)
* qemux86-64
* qemuarm
* qemuarm64
* qemuppc
* qemumips

Example:

    MACHINE = "qemuarm"

Now ready for building.
Build Linux kernel and the minimal rootfs by the following command.
It takes a while to complete (more than 30 minutes).

    $ bitbake core-image-minimal

Run images on QEMU
------------------

Run the images built in the above step on QEMU.
Please replace `${MACHINE}` by the target machine you selected in the above step.

    $ runqemu ${MACHINE} nographic

After boot, you can login as `root` without password.

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
