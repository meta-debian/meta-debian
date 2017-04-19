SUMMARY = "Perl module to script telnetable connections"
DESCRIPTION = "\
Net::Telnet allows you to make client connections to a TCP port and \
do network I/O, especially to a port using the TELNET protocol. \
Simple I/O methods such as print, get, and getline are provided. \
More sophisticated interactive features are provided because \
connecting to a TELNET port ultimately means communicating with a \
program designed for human interaction.  These interactive features \
include the ability to specify a timeout and to wait for patterns to \
appear in the input stream, such as the prompt from a shell. \
"
HOMEPAGE = "https://metacpan.org/release/Net-Telnet/"
PR = "r0"
inherit debian-package
PV = "3.04"

LICENSE = "Artistic-1.0 | GPL-1+"
LIC_FILES_CHKSUM = "\
	file://lib/Net/Telnet.pm;beginline=3;endline=5;md5=595feda9e8536d0a35f71ad7c693e4f2"
inherit cpan
