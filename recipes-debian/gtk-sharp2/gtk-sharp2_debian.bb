SUMMARY = "GTK# 2.10 suite, CLI bindings for GTK+"
DESCRIPTION = "\
GTK# 2.10 is a CLI (.NET) language binding for the GTK+ 2.10 toolkit \
gtk-sharp2 is a metapackage containing dependencies for the GTK# 2.10 suite.\
"
HOMEPAGE = "http://www.mono-project.com/GtkSharp"

PR = "r1"
inherit debian-package
PV = "2.12.10"

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"
inherit autotools-brokensep pkgconfig

#This patch correct name of POLICY_CONFIG files
SRC_URI += "file://Correct-Policy-Config_debian.patch"

export VERSION = "2.0"

EXTRA_OECONF += "\
	RUNTIME=${STAGING_BINDIR_NATIVE}/mono \
	CSC=${STAGING_BINDIR_NATIVE}/mono-csc \
"
DEPENDS += "gtk+ mono-native glib-2.0 pango cairo libglade"
PARALLEL_MAKE = ""

do_compile() {
	oe_runmake RUNTIME=${STAGING_BINDIR_NATIVE}/mono
}

#install follow Debian jessie
do_install() {
	oe_runmake install RUNTIME=${STAGING_BINDIR_NATIVE}/mono DESTDIR=${D} \
		GACUTIL_FLAGS="/package gtk-sharp-$VERSION /gacdir ${D}${libdir}"

	#follow debian/*.install
	install -d ${D}${datadir}/${PN}-examples/GtkDemo
	install -m 0644 ${S}/sample/*.cs ${D}${datadir}/${PN}-examples/
	install -m 0644 ${S}/sample/*.exe ${D}${datadir}/${PN}-examples/
	install -m 0644 ${S}/sample/GtkDemo/*.cs \
		${D}${datadir}/${PN}-examples/GtkDemo
	install -m 0644 ${S}/sample/GtkDemo/*.exe \
		${D}${datadir}/${PN}-examples/GtkDemo
	install -m 0644 ${S}/sample/Makefile ${D}${datadir}/${PN}-examples/
	cp -r ${S}/sample/pixmaps ${D}${datadir}/${PN}-examples/

	for file in atk gdk glib gtk pango glade; do
		install -d ${D}${libdir}/cli/$file-sharp-$VERSION
		mv ${D}${libdir}/mono/gac/$file-sharp/*/*.dll* \
			${D}${libdir}/cli/$file-sharp-$VERSION/
		mv ${D}${libdir}/lib${file}sharpglue-2.so \
			${D}${libdir}/cli/$file-sharp-$VERSION
	done
	install -d ${D}${libdir}/cli/gtk-dotnet-$VERSION
	mv ${D}${libdir}/mono/gac/gtk-dotnet/*/*.dll* \
		${D}${libdir}/cli/gtk-dotnet-$VERSION/

	rm ${D}${libdir}/*.la ${D}${libdir}/*.a

	install -m 0755 ${S}/debian/list-examples \
		${D}${bindir}/gtk-sharp2-examples-list
	
	install -d ${D}${datadir}/cli-common/packages.d
	install -d ${D}${datadir}/cli-common/policies.d

	install -m 0644 ${S}/debian/*.installcligac \
		${D}${datadir}/cli-common/packages.d
}

#install follow dh-cligacpolicy script
do_install_append() {
	cd ${S}/debian
	sed -i -e '/^#/d' *.cligacpolicy
	for file in *.cligacpolicy ; do
		assembly=${file%.*};
		install -d ${D}${datadir}/cli-common/policies.d/$assembly

		while read line; do
			name=`echo $line | cut -d" " -f2`;
			ver_tmp=`echo $line | cut -d" " -f3`;
			version="${ver_tmp%.*.*}"
			priority=`echo $line | cut -d" " -f5`;
			echo "${datadir}/cli-common/policies.d/$assembly/policy.$version.$name.dll" > \
				${D}${datadir}/cli-common/policies.d/0$priority-$version.$name

			mv ${D}${libdir}/mono/gac/*/*/policy.$version.$name.dll \
				${D}${datadir}/cli-common/policies.d/$assembly
			mv ${D}${libdir}/mono/gac/*/*/policy.$version.$name.config \
				${D}${datadir}/cli-common/policies.d/$assembly
		done < $file
	done
	rm -r ${D}${libdir}/mono
}

PACKAGES =+ "\
	${PN}-examples ${PN}-gapi libglade-cil libglade-cil-dev libglib-cil \
	libglib-cil-dev libgtk-cil monodoc-gtk-manual"

FILES_${PN}-examples = "\
	${bindir}/${PN}-examples-list ${datadir}/${PN}-examples/*"

FILES_${PN}-gapi = "\
	${bindir}/gapi2* ${libdir}/gtk-sharp-*/gapi* \
	${libdir}/pkgconfig/gapi*"

FILES_libglade-cil = "\
	${libdir}/cli/glade-sharp*/* \
	${datadir}/cli-common/policies.d/*.glade-sharp \
	${datadir}/cli-common/policies.d/libglade* \
	${datadir}/cli-common/packages.d/libglade*"

FILES_libglade-cil-dev = "\
	${libdir}/pkgconfig/glade* ${datadir}/gapi-*/glade-api.xml"

FILES_libglib-cil = "\
	${libdir}/cli/glib-sharp*/* \
	${datadir}/cli-common/policies.d/*.glib-sharp \
	${datadir}/cli-common/policies.d/libglib* \
	${datadir}/cli-common/packages.d/libglib*"
FILES_libglib-cil-dev = "\
	${libdir}/pkgconfig/glib* ${datadir}/gapi-*/glib-api.xml"

FILES_libgtk-cil = "\
	${libdir}/cli/atk-sharp*/* ${libdir}/cli/gdk-sharp*/* \
	${libdir}/cli/gtk*/* ${libdir}/cli/pango-sharp*/* \
	${datadir}/cli-common/packages.d/libgtk* \
	${datadir}/cli-common/policies.d/libgtk* \
	${datadir}/cli-common/policies.d/*-sharp \
	${datadir}/cli-common/policies.d/*dotnet"

FILES_${PN}-dev = "${libdir}/pkgconfig/gtk* ${datadir}/gapi*"

FILES_monodoc-gtk-manual = "${libdir}/monodoc"

FILES_${PN}-dbg += "${libdir}/cli/*/.debug"
#correct packages name:
PKG_libglade-cil = "libglade${VERSION}-cil"
PKG_libglade-cil-dev = "libglade${VERSION}-cil-dev"
PKG_libglib-cil = "libglib${VERSION}-cil"
PKG_libglib-cil-dev = "libglib${VERSION}-cil-dev"
PKG_libgtk-cil =  "libgtk${VERSION}-cil"
PKG_${PN}-dev = "libgtk${VERSION}-cil-dev"
PKG_monodoc-gtk-manual = "monodoc-gtk${VERSION}-manual"
