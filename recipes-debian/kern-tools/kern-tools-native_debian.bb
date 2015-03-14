DESCRIPTION = "merge_config.sh - Takes a list of config fragment values, and \
merges them one by one. Provides warnings on overridden values, and specified \      
values that did not make it to the resulting .config file (due to missed \
dependencies or config symbol removal)"

inherit native

require recipes-kernel/linux/linux-shared-source.inc
require recipes-kernel/linux/linux-ltsi-common.inc

do_configure() {
	:
}

do_compile() {
	:
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/scripts/kconfig/merge_config.sh ${D}${bindir}
}
