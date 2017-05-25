#
# base recipe: meta/recipes-core/busybox/busybox_1.22.1.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "1.22.0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de10de48642ab74318e893a61105afbb"

#
# controllable variables
#

# Whether to split the suid apps into a seperate binary
BUSYBOX_SPLIT_SUID ?= "1"

# config that are enabled/disabled according to VIRTUAL-RUNTIME_init_manager
BUSYBOX_INIT_CONFIGS ?= "INIT FEATURE_USE_INITTAB"

# space-separated list of the terminals that allow login
# (same format as SERIAL_CONSOLES)
# respawning gettys for ttys in this list are automatically appended to inittab
BUSYBOX_INITTAB_GETTYS ?= \
"${@base_conditional('SERIAL_CONSOLES', None or '', '38400;tty1', '${SERIAL_CONSOLES}', d)}"

#
# basic definitions
#

# busybox-appletlib-dependency.patch: avoid build process races
# 0001-build-system...patch: required for cross compiling
# defconfig: based on ${S}/debian/config/pkg/deb, and some configs are modified
#            so that the default system works correctly (see header comments)
SRC_URI += " \
file://busybox-appletlib-dependency.patch \
file://0001-build-system-Specify-nostldlib-when-linking-to-.o-fi.patch \
file://0001-menuconfig-check-lxdiaglog.sh-Allow-specification-of.patch \
file://defconfig \
file://inittab \
file://rcS \
file://run-ptest \
"

# settings for cross-compile
export EXTRA_CFLAGS = "${CFLAGS}"
export EXTRA_LDFLAGS = "${LDFLAGS}"
export EXTRA_OEMAKE += "'LD=${CCLD}' V=1 ARCH=${TARGET_ARCH} CROSS_COMPILE=${TARGET_PREFIX} SKIP_STRIP=y"

inherit cml1
inherit merge-config

do_configure () {
	# defconfig is the base configuration.
	# If they are .cfg files in SRC_URI, they are automatically
	# appended to defconfig in order of appearance.
	merge_config ${WORKDIR}/defconfig ${@" ".join(find_cfgs(d))}

	# enable/disable BUSYBOX_INIT_CONFIGS based on
	# VIRTUAL-RUNTIME_init_manager
	rm -f ${S}/.config.init
	for cfg in ${BUSYBOX_INIT_CONFIGS}; do
		if [ "${VIRTUAL-RUNTIME_init_manager}" = "busybox" ]; then
			cfg_def="CONFIG_${cfg}=y"
		else
			cfg_def="# CONFIG_${cfg} is not set"
		fi
		echo "${cfg_def}" >> ${S}/.config.init
	done
	merge_config .config ${S}/.config.init
	rm ${S}/.config.init

	cml1_do_configure
}

do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS

	# FEATURE_INDIVIDUAL produces not a symlink but a binary for each applet,
	# so no need to build two busybox binaries
	if [ "${BUSYBOX_SPLIT_SUID}" = "1" -a x`grep "CONFIG_FEATURE_INDIVIDUAL=y" .config` = x ]; then
		# split the .config into two parts, and make two busybox binaries

		cp .config .config.orig
		rm -f .config.suid .config.nosuid

		# busybox.cfg.suid/nosuid are the default rules in Makefile.custom.
		# They have only CONFIGs for applets that does/doesn't require SUID.
		oe_runmake busybox.cfg.suid
		oe_runmake busybox.cfg.nosuid

		# .config.disable.apps:
		#   all applet CONFIGs are disabled
		#   non-applet CONFIGs are not included
		for i in `cat busybox.cfg.suid busybox.cfg.nosuid`; do
			echo "# $i is not set" >> .config.disable.apps
		done

		# .config.noapps:
		#   all applet CONFIGs are disable
		#   non-applet CONFIGs are the same as .config.orig
		merge_config .config.orig .config.disable.apps
		cp .config .config.nonapps

		for s in suid nosuid; do
			# .config.app.suid/nosuid:
			#   all suid/nosuid applet CONFIGs are the same as .config.orig
			#   all nosuid/suid applet CONFIGs are not included
			#   non-applet CONFIGs are not included
			cat busybox.cfg.$s | while read item; do
				grep -w "$item" .config.orig
			done > .config.app.$s

			# .config (the final configuration):
			#   all suid/nosuid applet CONFIGs are the same as .config.orig
			#   all nosuid/suid applet CONFIGs are disabled
			#   non-applet CONFIGs are the same as .config.orig
			merge_config .config.nonapps .config.app.$s

			# make busybox binaries and busybox.links
			oe_runmake busybox_unstripped
			mv busybox_unstripped busybox.$s
			oe_runmake busybox.links
			mv busybox.links busybox.links.$s

			# keep configs for busybox.suid and busybox.nosuid
			cp .config .config.${s}
		done
		# copy .config.orig back to .config, because the install process may check this file
		cp .config.orig .config
		# cleanup
		rm .config.orig .config.app.suid .config.app.nosuid .config.disable.apps .config.nonapps
	else
		oe_runmake busybox_unstripped
		cp busybox_unstripped busybox
		oe_runmake busybox.links
	fi
}

do_install() {
	if [ "${prefix}" != "/usr" ]; then
		sed -i "s:^/usr/:${prefix}/:" busybox.links*
	fi
	if [ "${base_sbindir}" != "/sbin" ]; then
		sed -i "s:^/sbin/:${base_sbindir}/:" busybox.links*
	fi

	install -d ${D}${sysconfdir}/init.d

	# install busybox binaries, busybox.links files,
	# and several symlinks for postinst script
	if ! grep -q "CONFIG_FEATURE_INDIVIDUAL=y" ${B}/.config; then
		# Install /bin/busybox, and the /bin/sh link so the postinst script
		# can run. Let update-alternatives handle the rest.
		install -d ${D}${base_bindir}
		if [ "${BUSYBOX_SPLIT_SUID}" = "1" ]; then
			install -m 4755 ${B}/busybox.suid ${D}${base_bindir}
			install -m 0755 ${B}/busybox.nosuid ${D}${base_bindir}
			install -m 0644 ${S}/busybox.links.suid ${D}${sysconfdir}
			install -m 0644 ${S}/busybox.links.nosuid ${D}${sysconfdir}
			if grep -q "CONFIG_FEATURE_SH_IS_ASH=y" ${B}/.config; then
				ln -sf busybox.nosuid ${D}${base_bindir}/sh
			fi
			# Keep a default busybox for people who want to invoke busybox directly.
			# This is also useful for the on device upgrade. Because we want
			# to use the busybox command in postinst.
			ln -sf busybox.nosuid ${D}${base_bindir}/busybox
		else
			if grep -q "CONFIG_FEATURE_SUID=y" ${B}/.config; then
				install -m 4755 ${B}/busybox ${D}${base_bindir}
			else
				install -m 0755 ${B}/busybox ${D}${base_bindir}
			fi
			install -m 0644 ${S}/busybox.links ${D}${sysconfdir}
			if grep -q "CONFIG_FEATURE_SH_IS_ASH=y" ${B}/.config; then
				ln -sf busybox ${D}${base_bindir}/sh
			fi
			# We make this symlink here to eliminate the error when upgrading together
			# with busybox-syslog. Without this symlink, the opkg may think of the
			# busybox.nosuid as obsolete and remove it, resulting in dead links like
			# /bin/sed -> /bin/busybox.nosuid. This will make upgrading busybox-syslog fail.
			# This symlink will be safely deleted in postinst, thus no negative effect.
			ln -sf busybox ${D}${base_bindir}/busybox.nosuid
		fi
	else
		install -d ${D}${base_bindir} ${D}${base_sbindir}
		install -d ${D}${libdir} ${D}${bindir} ${D}${sbindir}
		cat busybox.links | while read FILE; do
			NAME=`basename "$FILE"`
			install -m 0755 "0_lib/$NAME" "${D}$FILE.${BPN}"
		done
		# add suid bit where needed
		for i in `grep -E "APPLET.*BB_SUID_((MAYBE|REQUIRE))" include/applets.h | \
			grep -v _BB_SUID_DROP | cut -f 3 -d '(' | cut -f 1 -d ','`; do
			find ${D} -name $i.${BPN} -exec chmod a+s {} \;
		done
		install -m 0755 0_lib/libbusybox.so.${PV} ${D}${libdir}/libbusybox.so.${PV}
		ln -sf sh.${BPN} ${D}${base_bindir}/sh
		ln -sf ln.${BPN} ${D}${base_bindir}/ln
		ln -sf test.${BPN} ${D}${bindir}/test
		if [ -f ${D}/linuxrc.${BPN} ]; then
			mv ${D}/linuxrc.${BPN} ${D}/linuxrc
		fi
		install -m 0644 ${S}/busybox.links ${D}${sysconfdir}
	fi

	# install scripts and configs for sub packages;
	# udhcpc, udhcpd, and syslogd
	install -d ${D}${sysconfdir}/default
	if grep -q "CONFIG_UDHCPC=y" ${B}/.config; then
		install -d ${D}${sysconfdir}/udhcpc
		install -m 0755 \
			${S}/debian/tree/udhcpc/etc/udhcpc/default.script \
			${D}${sysconfdir}/udhcpc
	fi
	if grep -q "CONFIG_UDHCPD=y" ${B}/.config; then
		install -m 0755 ${S}/debian/tree/udhcpd/etc/init.d/udhcpd \
			${D}${sysconfdir}/init.d
		install -m 0644 ${S}/debian/tree/udhcpd/etc/default/udhcpd \
			${D}${sysconfdir}/default
		install -m 0644 ${S}/debian/tree/udhcpd/etc/udhcpd.conf \
			${D}${sysconfdir}
	fi
	if grep -q "CONFIG_SYSLOGD=y" ${B}/.config; then
		install -m 0755 \
			${S}/debian/busybox-syslogd.busybox-klogd.init \
			${D}${sysconfdir}/init.d/busybox-klogd
		install -m 0755 ${S}/debian/busybox-syslogd.init \
			${D}${sysconfdir}/init.d/busybox-syslogd
		install -m 0644 ${S}/debian/busybox-syslogd.default \
			${D}${sysconfdir}/default/busybox-syslogd
		# drop insserv config file for sysvinit
	fi

	# install inittab and rcS only if busybox is the init manager
	if [ "${VIRTUAL-RUNTIME_init_manager}" = "busybox" ]; then
		install -m 0644 ${WORKDIR}/inittab ${D}${sysconfdir}

		# automatically append respawns for ${BUSYBOX_INITTAB_GETTYS}
		for gettyargs in $(echo "${BUSYBOX_INITTAB_GETTYS}" | tr ";" ":"); do
			baudrate=$(echo "${gettyargs}" | cut -d ":" -f 1)
			tty=$(echo "${gettyargs}" | cut -d ":" -f 2)
			echo "${tty}::respawn:${base_sbindir}/getty ${baudrate} ${tty}" \
				>> ${D}${sysconfdir}/inittab
		done

		install -m 0755 ${WORKDIR}/rcS ${D}${sysconfdir}/init.d
	fi
}

#
# packaging
#

PACKAGES =+ "${PN}-syslogd udhcpc udhcpd"

# sub packages don't include their core binaries (e.g. syslogd) and
# assume that the binaries are provided by the busybox main package
RDEPENDS_${PN}-syslogd += "${PN} lsb-base start-stop-daemon"
RDEPENDS_udhcpc += "${PN}"
RDEPENDS_udhcpd += "${PN} lsb-base start-stop-daemon"

# applets:
#   ${base_sbindir}/klogd
#   ${base_sbindir}/syslogd
#   ${base_bindir}/logread
FILES_${PN}-syslogd = "${sysconfdir}/default/busybox-syslogd \
                       ${sysconfdir}/init.d/busybox-klogd \
                       ${sysconfdir}/init.d/busybox-syslogd \
                      "
# applet: ${base_sbindir}/udhcpc
FILES_udhcpc = "${sysconfdir}/udhcpc/default.script"
# applets:
#   ${sbindir}/udhcpd
#   ${bindir}/dumpleases
FILES_udhcpd = "${sysconfdir}/default/udhcpd \
                ${sysconfdir}/init.d/udhcpd \
                ${sysconfdir}/udhcpd.conf \
               "

CONFFILES_udhcpd = "${sysconfdir}/udhcpd.conf"

#
# update-alternatives
#
# busybox package usually includes a lot of commands that are also
# provided by other packages. In order to avoid conflicts between them,
# update-alternatives parameters must be defined for all commands.
#
# busybox sub packages don't include commands
# (include only helper scripts and configs),
# so no need to define update-alternatives parameters for them.
#

inherit update-alternatives

ALTERNATIVE_PRIORITY = "50"

# Dynamically define update-alternatives parameters (ALTERNATIVE_*)
# for all commands in busybox.links* files. A simple example is below:
#
# /etc/busybox.links:
#   /bin/cmd1
#   /sbin/cmd2
#   ...
# results:
#   ALTERNATIVE_busybox = "cmd1 cmd2 ..."
#   ALTERNATIVE_LINK_NAME[cmd1] = "/bin/cmd1"
#   ALTERNATIVE_LINK_NAME[cmd2] = "/sbin/cmd2"
#   ...
#   ALTERNATIVE_TARGET[cmd1] = "/bin/busybox"
#   ALTERNATIVE_TARGET[cmd2] = "/bin/busybox"
#   ...
python do_package_prepend () {
    # We need to load the full set of busybox provides from the /etc/busybox.links
    # Use this to see the update-alternatives with the right information

    dvar = d.getVar('D', True)
    pn = d.getVar('PN', True)
    def set_alternative_vars(links, target):
        links = d.expand(links)
        target = d.expand(target)
        f = open('%s%s' % (dvar, links), 'r')
        for alt_link_name in f:
            alt_link_name = alt_link_name.strip()
            alt_name = os.path.basename(alt_link_name)
            # Match coreutils
            if alt_name == '[':
                alt_name = 'lbracket'
            d.appendVar('ALTERNATIVE_%s' % (pn), ' ' + alt_name)
            d.setVarFlag('ALTERNATIVE_LINK_NAME', alt_name, alt_link_name)
            if os.path.exists('%s%s' % (dvar, target)):
                d.setVarFlag('ALTERNATIVE_TARGET', alt_name, target)
        f.close()
        return

    if os.path.exists('%s/etc/busybox.links' % (dvar)):
        set_alternative_vars("/etc/busybox.links", "/bin/busybox")
    else:
        set_alternative_vars("/etc/busybox.links.nosuid", "/bin/busybox.nosuid")
        set_alternative_vars("/etc/busybox.links.suid", "/bin/busybox.suid")
}

#
# ptest
#

do_install_ptest () {
	cp -r ${B}/testsuite ${D}${PTEST_PATH}/
	cp ${B}/.config      ${D}${PTEST_PATH}/
	ln -s /bin/busybox   ${D}${PTEST_PATH}/busybox
}
