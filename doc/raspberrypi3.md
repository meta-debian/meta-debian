# Raspberry Pi

EMLinux for Raspberry Pi is __not__ officlally supported. However Raspberry Pi is popular device so it would be nice to test EMLinux on it.
This document describe how to setup EMLinux for Raspberry Pi 3 model B+.

## Directory structures

In this document, following directory structure is used.

```
build/
downloads/
emlinux/repos/meta-debian
emlinux/repos/meta-debian-extended
emlinux/repos/meta-emlinux
emlinux/repos/poky
firmware
linux-firmware
```

## Build EMLinux

1. Basic setup

Run following command.

```
$ source repos/meta-emlinux/scripts/setup-emlinux build
```

2. Build image

In the build directroy, you can build image. You needs to set raspberrypi3-64 to MACHINE variable.

```
$ MACHINE=raspberrypi3-64 bitbake core-image-minimal
```

## Create sdcard image

This document uses parted to create partions and /dev/sde as sdcard device.

1. Create fat32 partition for boot.

```
$ sudo parted /dev/sde mkpart primary fat32 1024KiB 65MiB
```

2. Create root partition

```
$ sudo parted /dev/sde mkpart primary ext4  65MiB 1GiB
```

3. Create fat32 file system on fat32 partition

```
$ sudo mkfs.vfat -n rpi -F32 /dev/sde1
```
_note: command line adds rpi label_

4. Create file ext4 file system on ext4 partition

```
$ sudo mkfs.ext4 -L rootfs /dev/sde2
```
_note: command line adds rootfs label_

5. Mount file systems

```
$ sudo mount /dev/sde1 /mnt/rpi
$ sudo mount /dev/sde2 /mnt/rootfs
```

## Copy Raspberry Pi firmwares

1. Clone firmware repository

```
$ git clone git@github.com:raspberrypi/firmware.git
```

2. Copy files to fat32 partition

```
$ cd firmware
$ sudo cp boot/start* /mnt/rpi/.
$ sudo cp boot/fixup* /mnt/rpi/.
$ sudo cp -r boot/overlays /mnt/rpi/.
$ sudo cp boot/bootcode.bin /mnt/rpi/.
```

## Copy kernel, dtb and u-boot

Go to your build directory. Then,

```
$ sudo cp tmp-glibc/deploy/images/raspberrypi3-64/Image /mnt/rpi/.
$ sudo cp tmp-glibc/deploy/images/raspberrypi3-64/bcm2837-rpi-3-b-plus.dtb /mnt/rpi/.
$ sudo cp tmp-glibc/deploy/images/raspberrypi3-64/u-boot.bin /mnt/rpi/.
```

##  Create config.txt

1. Create config.txt on /mnt/rpi

```
[all]
boot_delay=1
kernel=u-boot.bin

# Put the RPi3 into 64 bit mode
arm_control=0x200

upstream_kernel=1
audio_pwm_mode=0

# Enable UART
enable_uart=1

gpu_mem=16
mask_gpu_interrupt1=0x100
```

## Setup u-boot

1. Create boot.cmd file on any directory.

```
fatload mmc 0 ${kernel_addr_r} Image
fatload mmc 0 ${fdt_addr_r} bcm2837-rpi-3-b-plus.dtb
setenv bootargs dwc_otg.lpm_enable=0 earlyprintk root=/dev/mmcblk0p2 rootfstype=ext4 rootwait
booti ${kernel_addr_r} - ${fdt_addr_r}
```

2. Create .scr file

```
$ sudo mkimage -C none -A arm64 -T script -d ./boot.cmd /mnt/rpi/boot.scr
Image Name:
Created:      Thu Aug  8 11:46:12 2019
Image Type:   AArch64 Linux Script (uncompressed)
Data Size:    256 Bytes = 0.25 KiB = 0.00 MiB
Load Address: 00000000
Entry Point:  00000000
Contents:
   Image 0: 248 Bytes = 0.24 KiB = 0.00 MiB
```

3. (optional) Enable early UART support

If you want see u-boot's message by uart, you needs to run following command.

```
$ sudo sed -i -e "s/BOOT_UART=0/BOOT_UART=1/" /mnt/rpi/bootcode.bin
```

## Setup root file system

1. Extract root file system files to sdcard

Go to build directory then.

```
$ sudo tar xvf tmp-glibc/deploy/images/raspberrypi3-64/core-image-minimal-raspberrypi3-64.tar.gz -C /mnt/rootfs/
```

2. Clone linux firmwares from git repository

```
$ git clone git://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git
```

3. Create /lib/firmware on rootfs partition

```
$ sudo mkdir  /mnt/rootfs/lib/firmware
```

4. Copy firmwares

```
$ sudo cp -r linux-firmware/brcm/ /mnt/rootfs/lib/firmware/
```

5. Create symbolic link

```
$ cd /mnt/rootfs/lib/firmware/brcm
$ sudo ln -s brcmfmac43455-sdio.raspberrypi,3-model-b-plus.txt brcmfmac43455-sdio.txt
```

## Boot EMLinux

unmount sdcard and insert sdcart to your Raspberry Pi then start it. You can login to console via UART.

```
EMLinux 2.0 raspberrypi3-64 /dev/ttyS1

raspberrypi3-64 login: [    3.675695] lan78xx 1-1.1.1:1.0 (unnamed net_device) (uninitialized): No External EEPROM. Setting MAC Speed
[    3.695249] libphy: lan78xx-mdiobus: probed

EMLinux 2.0 raspberrypi3-64 /dev/ttyS1

raspberrypi3-64 login: root
root@raspberrypi3-64:~#
```

If firmwares are loaded correctlly, you'll see wlan0 and eth0 devices.

```
root@raspberrypi3-64:~# ip a
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
2: wlan0: <BROADCAST,MULTICAST> mtu 1500 qdisc noop qlen 1000
    link/ether b8:27:eb:25:af:d2 brd ff:ff:ff:ff:ff:ff
3: eth0: <BROADCAST,MULTICAST> mtu 1500 qdisc noop qlen 1000
    link/ether b8:27:eb:70:fa:87 brd ff:ff:ff:ff:ff:ff
```

## Use network

### Use eth0

You can setup ip address as usual.

```
root@raspberrypi3-64:~# ip addr add 192.168.11.100/24 dev eth0
root@raspberrypi3-64:~# ip link set eth0 up
root@raspberrypi3-64:~# ip route add default via 192.168.11.1
root@raspberrypi3-64:~# ip addr show  eth0
3: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast qlen 1000
    link/ether b8:27:eb:70:fa:87 brd ff:ff:ff:ff:ff:ff
    inet 192.168.11.100/24 scope global eth0
       valid_lft forever preferred_lft forever
```

### Use wlan0

1. Add wpa-supplicant to image
You need to add following option in your conf/local.conf.

```
IMAGE_INSTALL_append += " wpa-supplicant"
```

2. Build image and setup root file system

See previous sections

3. Create configuration data via wpa_supplicant

```
root@raspberrypi3-64:~# wpa_passphrase <SSID> <PASSPHRASE>
```

4. Copy paste wpa_passphrase command output to /etc/wpa_supplicant.conf

remove default network section and paste wpa_passphrase output.

```
ctrl_interface=/var/run/wpa_supplicant
ctrl_interface_group=0
update_config=1

network={
       ssid="YOURSSID"
       psk=PSKSTRING
}
```

5. Setup ip address to device

This setup is same as eth0 use wlan0 instead of eth0.
