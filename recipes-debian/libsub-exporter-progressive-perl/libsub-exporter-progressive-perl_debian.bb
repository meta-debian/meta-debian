SUMMARY = "module for using Sub::Exporter only if needed"
DESCRIPTION = "Sub::Exporter is an incredibly powerful module, but with that power comes\n\
 great responsibility, as well as some runtime penalties.\n\
 Sub::Exporter::Progressive is a Sub::Exporter wrapper that will let your\n\
 users just use Exporter if all they are doing is picking exports, but use\n\
 Sub::Exporter if your users try to use Sub::Exporter's more advanced\n\
 features, like renaming exports.\n\
 .\n\
 Note that this module will export @EXPORT and @EXPORT_OK package variables\n\
 for Exporter to work. Additionally, if your package uses advanced\n\
 Sub::Exporter features like currying, this module will only use\n\
 Sub::Exporter, so you might as well use it directly."
HOMEPAGE = "https://metacpan.org/release/Sub-Exporter-Progressive/"

PR = "r0"
inherit debian-package
PV = "0.001011"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=47;endline=53;md5=d3a3390d2ea6dfc82d328abfe613d59d"

inherit cpan
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""
