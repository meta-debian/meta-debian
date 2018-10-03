#
# base recipe: meta/recipes-devtools/gdb/gdb_8.1.bb
# base branch: master
# base commit: b0f2f690a3513e4c9fa30fee1b8d7ac2d7140657
#

require recipes-devtools/gdb/gdb.inc
require gdb-8.1.inc

inherit python3-dir

EXTRA_OEMAKE_append_libc-musl = "\
                                 gt_cv_func_gnugettext1_libc=yes \
                                 gt_cv_func_gnugettext2_libc=yes \
                                 gl_cv_func_working_strerror=yes \
                                 gl_cv_func_strerror_0_works=yes \
                                 gl_cv_func_gettimeofday_clobber=no \
                                "

do_configure_prepend() {
	if [ "${@bb.utils.filter('PACKAGECONFIG', 'python', d)}" ]; then
		cat > ${WORKDIR}/python << EOF
#!/bin/sh
case "\$2" in
	--includes) echo "-I${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI}/" ;;
	--ldflags) echo "-Wl,-rpath-link,${STAGING_LIBDIR}/.. -Wl,-rpath,${libdir}/.. -lpthread -ldl -lutil -lm -lpython${PYTHON_BASEVERSION}${PYTHON_ABI}" ;;
	--exec-prefix) echo "${exec_prefix}" ;;
	*) exit 1 ;;
esac
exit 0
EOF
		chmod +x ${WORKDIR}/python
	fi
}
CFLAGS_append_libc-musl = " -Drpl_gettimeofday=gettimeofday"
