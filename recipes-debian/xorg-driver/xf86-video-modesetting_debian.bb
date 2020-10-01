SUMMARY = "X.Org X server -- Generic modesetting driver"
DESCRIPTION = "This package provides a generic modesetting driver.\n\
 .\n\
 More information about X.Org can be found at:\n\
 <URL:http://www.X.org>\n\
 .\n\
 This package is built from the X.org xf86-video-modesetting driver module."

require xorg-driver-video.inc
PV = "0.9.0"
DPN = "xserver-xorg-video-modesetting"

LIC_FILES_CHKSUM = " \
	file://COPYING;md5=5e53d3fcadb1c23d122ad63cb099a918 \
"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

do_install_append(){
	install -d ${D}${sysconfdir}/modprobe.d
	install -m 0644 ${S}/debian/modesetting.conf ${D}${sysconfdir}/modprobe.d
}
