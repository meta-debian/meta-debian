#
# base recipe: meta/recipes-extended/net-tools/net-tools_1.60-26.bb
# base branch: master
# base commit: bf362e4a8bb9fef3d16b81dea7b39a057e293ee4
#

PR = "r0"

inherit debian-package
PV = "1.60"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
	file://ifconfig.c;beginline=11;endline=15;md5=d1ca372080ad5401e23ca0afc35cf9ba \
"

DEBIAN_PATCH_TYPE = "quilt"

inherit gettext update-alternatives

# The Makefile is lame, no parallel build
PARALLEL_MAKE = ""

do_configure() {
	# net-tools has its own config mechanism requiring "make config"
	# we use options from debian and copy to source directory instead
	cp ${S}/debian/config.* ${S}/
}

do_compile() {
	# net-tools use COPTS/LOPTS to allow adding custom options
	export COPTS="$CFLAGS"
	export LOPTS="$LDFLAGS"
	unset CFLAGS
	unset LDFLAGS

	oe_runmake PROGS="ifconfig arp netstat route rarp slattach plipconfig \
			nameif iptunnel ipmaddr mii-tool"
}

# Follow debian/install
do_install() {
	sbin_files="arp"
	base_sbin_files="ifconfig nameif plipconfig rarp route slattach ipmaddr iptunnel mii-tool"
	base_bin_files="netstat"

	install -d ${D}${sbindir} ${D}${base_sbindir} ${D}${base_bindir}
	for i in $sbin_files; do
		install -m 0755 $i ${D}${sbindir}
	done
	for i in $base_sbin_files; do
		install -m 0755 $i ${D}${base_sbindir}
	done
	for i in $base_bin_files; do
		install -m 0755 $i ${D}${base_bindir}
	done
}

# Add update-alternatives definitions
ALTERNATIVE_PRIORITY="100"

base_sbindir_progs = "ifconfig iptunnel nameif route slattach"
ALTERNATIVE_${PN} = "${base_sbindir_progs} netstat"
ALTERNATIVE_LINK_NAME[netstat] = "${base_bindir}/netstat"
python __anonymous() {
        for prog in d.getVar('base_sbindir_progs', True).split():
                d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_sbindir', True), prog))
}
