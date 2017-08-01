SUMMARY = "module using libc functions for internationalization in Perl"
DESCRIPTION = "The gettext module permits access from perl to the gettext() family of\n\
functions for retrieving message strings from databases constructed\n\
to internationalize software.\n\
.\n\
It provides gettext(), dgettext(), dcgettext(), textdomain(),\n\
bindtextdomain(), bind_textdomain_codeset(), ngettext(), dcngettext()\n\
and dngettext()."
HOMEPAGE = "https://metacpan.org/release/gettext"

inherit debian-package
PV = "1.05"

LICENSE = "Artistic-1.0 | GPL-1+"
LIC_FILES_CHKSUM = "file://README;beginline=11;endline=16;md5=9582589df57b78314e5f393adf890785"

inherit cpan
