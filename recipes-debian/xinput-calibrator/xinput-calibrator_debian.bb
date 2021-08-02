SUMMARY = "Generic touchscreen calibration program for X.Org"
DESCRIPTION = " xinput-calibrator is a tool to calibrate touchscreens under X.Org. \
 Its features include: \
  - work for any Xorg driver (use Xinput to get axis valuators); \
  - output the calibration as Xorg.conf, HAL policy and udev rule; \
  - support advanced driver options, such as Evdev's dynamic calibration; \
  - have a very intuitive GUI (normal X client);"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/xinput_calibrator/"

inherit debian-package
PV = "0.7.5+git20140201"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d9a6926aaad6b8f38bb4f995fe088466 \
"

inherit autotools distro_features_check

DEPENDS += "libx11 libxi"

REQUIRED_DISTRO_FEATURES = "x11"
EXTRA_OECONF += "--with-gui=x11"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm).*-linux"
