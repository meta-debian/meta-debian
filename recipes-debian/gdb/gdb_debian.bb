#
# Base recipe: recipes-devtools/gdb/gdb_7.6.2.bb
# Base branch: daisy
#

require gdb-common.inc

inherit gettext

# cross-canadian must not see this
PACKAGES =+ "gdbserver"
FILES_gdbserver = "${bindir}/gdbserver"

inherit python-dir

PACKAGECONFIG ??= ""
PACKAGECONFIG[python] = "--with-python=${WORKDIR}/python,--without-python,python"
PACKAGECONFIG[babeltrace] = "--with-babeltrace,--without-babeltrace,babeltrace"

do_configure_prepend() {
        if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'python', 'python', '', d)}" ]; then
                cat > ${WORKDIR}/python << EOF
#!/bin/sh
case "\$2" in
        --includes) echo "-I${STAGING_INCDIR}/${PYTHON_DIR}/" ;;
        --ldflags) echo "-Wl,-rpath-link,${STAGING_LIBDIR}/.. -Wl,-rpath,${libdir}/.. -lpthread -ldl -lutil -lm -lpython${PYTHON_BASEVERSION}" ;;
        --exec-prefix) echo "${exec_prefix}" ;;
        *) exit 1 ;;
esac
exit 0
EOF
                chmod +x ${WORKDIR}/python
        fi
}

do_install_append() {
	# Install init script follow debian
	install -d ${D}${sysconfdir}/${BPN}
	install -m 0644 ${S}/debian/gdbinit ${D}${sysconfdir}/${BPN}
	install -m 0755 ${S}/gdb/contrib/gdb-add-index.sh \
			${D}${bindir}/gdb-add-index
	install -m 0755 ${S}/debian/gdbtui ${D}${bindir}
}
