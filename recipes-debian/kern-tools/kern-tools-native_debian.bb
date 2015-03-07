DESCRIPTION = "merge_config.sh - Takes a list of config fragment values, and \
merges them one by one. Provides warnings on overridden values, and specified \      
values that did not make it to the resulting .config file (due to missed \
dependencies or config symbol removal)"

inherit kernel-checkout native

LINUX_SRCREV = "linux-3.10.y-zynq-backport"
LINUX_VERSION = "3.10.24"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/scripts/kconfig/merge_config.sh ${D}${bindir}
}
