SUMMARY = "Traces the route taken by packets over an IPv4/IPv6 network"
DESCRIPTION = "The traceroute utility displays the route used by IP packets on their way to a \
specified network (or Internet) host. Traceroute displays the IP number and \
host name (if possible) of the machines along the route taken by the packets. \
Traceroute is used as a network debugging tool. If you're having network \
connectivity problems, traceroute will show you where the trouble is coming \
from along the route. \
Install traceroute if you need a tool for diagnosing network connectivity \
problems."
HOMEPAGE = "http://traceroute.sourceforge.net/"

PR = "r0"

inherit debian-package
PV = "2.0.20"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit update-alternatives

# Avoid error:
#   "make[1]: *** No rule to make target '-lm', needed by 'traceroute'.  Stop."
EXTRA_OEMAKE = "VPATH=${STAGING_LIBDIR}"

do_compile() {
	export LDFLAGS="${TARGET_LDFLAGS} -L${S}/libsupp"
	oe_runmake "env=yes"
}

do_install() {
	#
	# Follow debian/rules
	#
	install -d ${D}${bindir} ${D}${sbindir} ${D}${mandir}/man1 ${D}${mandir}/man8

	install -D -m 0755 traceroute/traceroute ${D}${bindir}/traceroute.db
	install -D -m 0644 traceroute/traceroute.8 ${D}${mandir}/man1/traceroute.db.1

	install -D -m 0755 wrappers/traceroute-nanog ${D}${bindir}/traceroute-nanog
	ln -s traceroute.db.1 ${D}${mandir}/man1/traceroute-nanog.1

	install -D -m 0644 libsupp/libsupp.a ${D}${libdir}/libsupp.a
	install -D -m 0644 libsupp/clif.h ${D}${includedir}/clif.h

	for _ALTERNATIVE in traceroute6; do
		ln -s traceroute.db ${D}${bindir}/${_ALTERNATIVE}.db
		ln -s traceroute.db.1 ${D}${mandir}/man1/${_ALTERNATIVE}.db.1
	done

	# Installing wrappers
	for _WRAPPER in lft traceproto;	do
		install -D -m 0755 wrappers/${_WRAPPER} ${D}${bindir}/${_WRAPPER}.db
		ln -s traceroute.db.1 ${D}${mandir}/man1/${_WRAPPER}.db.1
	done

	for _WRAPPER in tcptraceroute; do
		install -D -m 0755 wrappers/${_WRAPPER} ${D}${sbindir}/${_WRAPPER}.db
		ln -s ../man1/traceroute.db.1 ${D}${mandir}/man8/${_WRAPPER}.db.8
	done
}

# Follow debian/traceroute.postinst
bin_ALTERNATIVES = "traceroute traceroute6 lft traceproto"
sbin_ALTERNATIVES = "tcptraceroute"

python do_package_prepend() {
    bin_alts = d.getVar("bin_ALTERNATIVES", True) or ""
    bindir = d.getVar("bindir", True)
    for alt in bin_alts.split():
        d.setVarFlag('ALTERNATIVE_TARGET', alt, bindir + "/" + alt + ".db")
        d.setVarFlag('ALTERNATIVE_LINK_NAME', alt, bindir + "/" + alt)

    sbin_alts = d.getVar("sbin_ALTERNATIVES", True)
    sbindir = d.getVar("sbindir", True)
    for alt in sbin_alts.split():
        d.setVarFlag('ALTERNATIVE_TARGET', alt, sbindir + "/" + alt + ".db")
        d.setVarFlag('ALTERNATIVE_LINK_NAME', alt, sbindir + "/" + alt)
}

ALTERNATIVE_${PN} = "${bin_ALTERNATIVES} ${sbin_ALTERNATIVES}"
ALTERNATIVE_PRIORITY = "100"
