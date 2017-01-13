inherit debian-package
PV = "1.15"
DEBIAN_PATCH_TYPE = "nopatch"

SUMMARY = "store /etc in git, mercurial, bzr or darcs"
DESCRIPTION = "\
The etckeeper program is a tool to let /etc be stored in a git, mercurial, \
bzr or darcs repository. It hooks into APT to automatically commit changes \
made to /etc during package upgrades. It tracks file metadata that version \
control systems do not normally support, but that is important for /etc, such \
as the permissions of /etc/shadow. It's quite modular and configurable, while \
also being simple to use if you understand the basics of working with version \
control."
HOMEPAGE = "http://kitenet.net/~joey/code/etckeeper/"
SECTION = "admin"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://GPL;md5=751419260aa954499f7abaabaa882bbe"
RDEPENDS_${PN} += " findutils perl perl-module-file-glob"
RRECOMMENDS_${PN} += " git"

inherit allarch

do_install() {
	oe_runmake install DESTDIR=${D}

}
