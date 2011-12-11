#!/bin/sh -x

VERSION=1.3-SNAPSHOT
REMOTE=false;
[ "$1" = "-r" ] && REMOTE=true;

die () { echo "ERROR: $*" >&2; exit 1; }

DIST_DIR=~/dist-new
F_CT=javiator-controlterminal/target/javiator-controlterminal-${VERSION}-linux.tar.gz
F_MJ=javiator-mockjaviator/target/javiator-mockjaviator-${VERSION}.tar.gz
F_JC=jnavigator-jcontrol/target/jnavigator-jcontrol-${VERSION}-linux.tar.gz
F_NL=jnavigator-lab/target/jnavigator-lab-${VERSION}.tar.gz
F_UI=jnavigator-ui/target/jnavigator-ui-${VERSION}.tar.gz
F_NT=jnavigator-terminal/target/jnavigator-terminal-${VERSION}-linux.tar.gz
FILES="$F_CT $F_MJ $F_JC $F_NL $F_UI $F_NT";

mvn clean install -Dmaven.test.skip

[ -d $DIST_DIR ] || mkdir $DIST_DIR
[ -d $DIST_DIR ] || die "Can not create folder $DIST_DIR";

cp $F_CT $F_MJ $F_JC $F_NL $F_UI $F_NT $DIST_DIR

(
   cd $DIST_DIR || die "Can not chdir() to folder $DIST_DIR";

   rm -rf controlterminal jnavigator-ui jnavigator-jcontrol javiator-mockjaviator jnavigator-terminal

   mkdir controlterminal jnavigator-ui jnavigator-jcontrol javiator-mockjaviator jnavigator-terminal

   (cd controlterminal       && tar -xzf ../$(basename $F_CT))
   (cd javiator-mockjaviator && tar -xzf ../$(basename $F_MJ))
   (cd jnavigator-jcontrol   && tar -xzf ../$(basename $F_JC))
   (cd jnavigator-ui         && tar -xzf ../$(basename $F_UI))
   (cd jnavigator-terminal   && tar -xzf ../$(basename $F_NT))
   (cd java3D                && cp * ../controlterminal/lib && cp * ../jnavigator-terminal/lib )
)


$REMOTE || exit 0;

SCP=scp; SSH=ssh; HOST="ckgroup-2";
scp $F_CT $F_MJ $F_JC $F_NL $F_UI $HOST:$DIST_DIR
#$SSH $HOST "cd ~/dist-new && ./inst.sh"

echo "Remote: ";
echo "
set -x
cd $DIST_DIR
rm -rf controlterminal jnavigator-ui jnavigator-jcontrol javiator-mockjaviator
mkdir controlterminal jnavigator-ui jnavigator-jcontrol javiator-mockjaviator
(cd controlterminal     && tar -xzf ../$(basename $F_CT))
(cd javiator-mockjaviator && tar -xzf ../$(basename $F_MJ))
(cd jnavigator-jcontrol && tar -xzf ../$(basename $F_JC))
(cd jnavigator-ui       && tar -xzf ../$(basename $F_UI))
(cd java3D              && cp * ../controlterminal/lib)
" | $SSH $HOST


