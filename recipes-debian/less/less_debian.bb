#
# base recipe: meta/recipes-extended/less/less_458.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "458"

# Including email author giving permissing to use BSD
#
# From: Mark Nudelman <markn@greenwoodsoftware.com>
# To: Elizabeth Flanagan <elizabeth.flanagan@intel.com
# Date: 12/19/11
#
# Hi Elizabeth,
# Using a generic BSD license for less is fine with me.
# Thanks,
#
# --Mark
#
LICENSE = "GPLv3+ | BSD-2-Clause"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://LICENSE;md5=866cc220f330b04ae4661fc3cdfedea7 \
"

DEPENDS = "ncurses"

inherit autotools update-alternatives

# On Debian, binary files are installed to /bin
EXTRA_OECONF += "--bindir=${base_bindir}"

# Follow debian/rules
do_install_append() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/debian/lesspipe ${D}${base_bindir}/lesspipe
	ln -s lesspipe ${D}${base_bindir}/lessfile
	ln -s ../../bin/lesspipe ${D}${bindir}/lessfile
	ln -s ../../bin/lesspipe ${D}${bindir}/lesspipe
	ln -s ../../bin/lesskey ${D}${bindir}/lesskey
	ln -s ../../bin/lessecho ${D}${bindir}/lessecho
	ln -s ../../bin/less ${D}${bindir}/less

	mkdir -p ${D}${libdir}/mime/packages
	install -m 0644 ${S}/debian/mime ${D}${libdir}/mime/packages/less
}

FILES_${PN} += "${libdir}/*"

# Add update-alternatives definitions to avoid conflict with busybox
ALTERNATIVE_${PN} = "less"
ALTERNATIVE_PRIORITY[less] = "100"
ALTERNATIVE_LINK_NAME[less] = "${base_bindir}/less"

# Follow debian/postinst
ALTERNATIVE_${PN} += "pager"
ALTERNATIVE_TARGET[pager] = "${base_bindir}/less.${DPN}"
ALTERNATIVE_LINK_NAME[pager] = "${bindir}/pager"
ALTERNATIVE_PRIORITY[pager] = "77"
