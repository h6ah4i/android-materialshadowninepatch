#!/bin/bash -e
#
#    Copyright (C) 2015 Haruki Hasegawa
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

src_dir=$1
dest_dir=$2
num_amb_spot_drawables=$3
num_composite_drawables=$4
res_prefix=$5

# resolve commands
if command -v parallel >/dev/null 2>&1; then
    # echo "GNU parallel is available"
    iterator_cmd=parallel
    iterator_cmd_opt="-a -"
else
    # echo "GNU parallel is not available"
    iterator_cmd=xargs
    iterator_cmd_opt="-i"
fi

crop_ninepatch_valid_area=./crop_ninepatch_valid_area.sh


# create destination directory 
if [ ! -d $dest_dir ]; then
    mkdir $dest_dir
fi


# crop
function batch_crop_niniepatch_valid_area {
    local src_dir=$1
    local dest_dir=$2
    local scale=$3
    local crop_pixels=$(( scale - 1 ))
    local target_list=$(find $src_dir -maxdepth 1 -type f -name "*_x"$scale"*.png" | gawk -F/ '{print $NF}' | sort -n)

    echo "$target_list" | \
        $iterator_cmd $iterator_cmd_opt \
        $crop_ninepatch_valid_area $src_dir/{} $dest_dir/{} $crop_pixels
}

batch_crop_niniepatch_valid_area $src_dir $dest_dir 1
batch_crop_niniepatch_valid_area $src_dir $dest_dir 2
batch_crop_niniepatch_valid_area $src_dir $dest_dir 3
batch_crop_niniepatch_valid_area $src_dir $dest_dir 4
