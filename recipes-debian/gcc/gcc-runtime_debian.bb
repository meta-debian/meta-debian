#
# base recipe: meta/recipes-devtools/gcc/gcc-runtime_8.1.bb
# base branch: master
# base commit: a5d1288804e517dee113cb9302149541f825d316
#

require gcc-8.1.inc
require recipes-devtools/gcc/gcc-runtime.inc

# Disable ifuncs for libatomic on arm conflicts -march/-mcpu
EXTRA_OECONF_append_arm = " libat_cv_have_ifunc=no "

FILES_libgomp-dev += "\
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/openacc.h \
"
