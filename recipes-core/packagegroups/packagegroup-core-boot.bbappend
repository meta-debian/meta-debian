RDEPENDS_${PN} += "update-alternatives"

# Follow Debian, busybox does not provide package busybox-hwclock.
# So, remove depend on "busybox-hwclock" in package-core-boot from poky.
# Also add depend on ifupdown in pacakge-core-boot from meta-debian,
# instead of init-ifupdown from poky.
SYSVINIT_SCRIPTS = "modutils-initscripts \
                    ifupdown \
                    ${VIRTUAL-RUNTIME_initscripts} \
                   "
