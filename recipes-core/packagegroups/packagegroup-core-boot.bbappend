RDEPENDS_${PN} += "update-alternatives"

# Follow Debian, busybox does not provide package busybox-hwclock.
# So, remove depend on "busybox-hwclock" in package-core-boot from poky.
SYSVINIT_SCRIPTS = "modutils-initscripts \
                    init-ifupdown \
                    ${VIRTUAL-RUNTIME_initscripts} \
                   "
