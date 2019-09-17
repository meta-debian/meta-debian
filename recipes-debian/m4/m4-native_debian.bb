# base recipe: meta/recipes-devtools/m4/m4-native_1.4.18.bb
# base branch: warrior

require m4.inc

inherit native

INHIBIT_AUTOTOOLS_DEPS = "1"
DEPENDS += "gnu-config-native"

do_configure() {
	install -m 0644 ${STAGING_DATADIR}/gnu-config/config.sub .
	install -m 0644 ${STAGING_DATADIR}/gnu-config/config.guess .
	oe_runconf
}
