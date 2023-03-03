#!/bin/bash
#
# Copyright (c) 2023.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

echo "###########################"
echo "# clean docker"
echo "###########################"

echo "# stopping containers"
docker stop `docker ps | awk '{print $1}' | grep -v CONTAINER` >> /dev/null 2>&1

echo "# prune system"
docker system prune -f --volumes >> /dev/null 2>&1

echo "###########################"
echo "# done"