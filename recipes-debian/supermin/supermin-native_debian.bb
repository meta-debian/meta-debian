SUMMARY = "tool for building supermin appliances."
DESCRIPTION = "Supermin appliances are tiny appliances, similar to virtual machine \
images, usually around 100KB in size, which get fully instantiated \
on-the-fly in a fraction of a second to a filesystem image when they \
are booted."
HOMEPAGE = "http://people.redhat.com/~rjones/supermin/"

inherit debian-package
PV = "5.1.11"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit autotools native

DEPENDS += "ocaml-native xz-utils-native e2fsprogs-native rpm-native findlib-native"

export EXT2FS_LIBS="-L${STAGING_LIBDIR_NATIVE} -lext2fs"
export COM_ERR_LIBS="-L${STAGING_LIBDIR_NATIVE} -lcom_err"
export LIBRPM_LIBS="-L${STAGING_LIBDIR_NATIVE} -lrpm -lrpmio -lbz2"

CACHED_CONFIGUREVARS += "ac_cv_prog_OCAMLMKLIB=${STAGING_BINDIR_NATIVE}/ocamlmklib \
                         ac_cv_prog_OCAMLMKTOP=${STAGING_BINDIR_NATIVE}/ocamlmktop \
                         ac_cv_prog_OCAMLDEP=${STAGING_BINDIR_NATIVE}/ocamldep \
                         ac_cv_prog_OCAMLDOC=${STAGING_BINDIR_NATIVE}/ocamldoc \
                         ac_cv_prog_OCAMLBUILD=${STAGING_BINDIR_NATIVE}/ocamlBUILD \
                         ac_cv_prog_OCAMLFIND=${STAGING_BINDIR_NATIVE}/ocamlfind \
                         ac_cv_prog_OCAMLCDOTOPT=${STAGING_BINDIR_NATIVE}/ocamlc.opt \
"

do_compile() {
	# Add rpath to compile supermin
	sed -i -e "s|-linkpkg|-linkpkg -ccopt \'-Wl,-rpath,${STAGING_LIBDIR_NATIVE}\'|" \
		${B}/src/supermin-link.sh
	# Add -ldl to avoid compile error:
	#| common.c:(.text+0x270): undefined reference to `dlopen'
	#| common.c:(.text+0x27d): undefined reference to `dlclose'
	oe_runmake LZMA_STATIC_LIBS="-L${STAGING_LIBDIR_NATIVE} -llzma -ldl"
}
