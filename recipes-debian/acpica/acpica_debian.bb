# base recipe: meta/recipes-extended/acpica/acpica_20180508.bb
# base branch: warrior

SUMMARY = "ACPICA tools for the development and debug of ACPI tables"
DESCRIPTION = "The ACPI Component Architecture (ACPICA) project provides an \
OS-independent reference implementation of the Advanced Configuration and \
Power Interface Specification (ACPI). ACPICA code contains those portions of \
ACPI meant to be directly integrated into the host OS as a kernel-resident \
subsystem, and a small set of tools to assist in developing and debugging \
ACPI tables."
HOMEPAGE = "http://www.acpica.org/"

LICENSE = "BSD | GPLv2"
LIC_FILES_CHKSUM = " \
    file://source/tools/acpibin/acpibin.h;beginline=7;endline=42;md5=afe4766a1a13eb9653b174a483ceb957 \
"

inherit debian-package
require recipes-debian/sources/acpica-unix.inc
BPN = "acpica-unix"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

DEPENDS = "bison-native"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "acpixtract"

EXTRA_OEMAKE = "CC='${CC}' 'OPT_CFLAGS=-Wall'"

do_install() {
    install -D -p -m0755 generate/unix/bin*/iasl ${D}${bindir}/iasl
    install -D -p -m0755 generate/unix/bin*/acpibin ${D}${bindir}/acpibin
    install -D -p -m0755 generate/unix/bin*/acpiexec ${D}${bindir}/acpiexec
    install -D -p -m0755 generate/unix/bin*/acpihelp ${D}${bindir}/acpihelp
    install -D -p -m0755 generate/unix/bin*/acpinames ${D}${bindir}/acpinames
    install -D -p -m0755 generate/unix/bin*/acpisrc ${D}${bindir}/acpisrc
    install -D -p -m0755 generate/unix/bin*/acpixtract ${D}${bindir}/acpixtract
}

BBCLASSEXTEND = "native"
