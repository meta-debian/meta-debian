SUMMARY = "Script SNMP connections"
DESCRIPTION = "The module Net::SNMP implements an object oriented interface to\n\
the Simple Network Management Protocol.  Perl applications can\n\
use the module to retrieve or update information on a remote\n\
host using the SNMP protocol.  Net::SNMP is implemented completely\n\
in Perl, requires no compiling, and uses only standard Perl \n\
modules.  SNMPv1 and SNMPv2c (Community-Based SNMPv2), as well as\n\
SNMPv3 with USM are supported by the module.  SNMP over UDP as well\n\
as TCP with both IPv4 and IPv6 can be used.  The Net::SNMP module\n\
assumes that the user has a basic understanding of the Simple\n\
Network Management Protocol and related network management concepts."
HOMEPAGE = "http://www.cpan.org/modules/by-module/Net/"
LICENSE = "Artistic-1.0 | GPL-1.0+"
SECTION = "perl"
DEPENDS = "perl netbase"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b408db04fd98f8acb73bb97027a934d5"

PR = "r0"
inherit debian-package
PV = "6.0.1"

inherit cpan allarch
