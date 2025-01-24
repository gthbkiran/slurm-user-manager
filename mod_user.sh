#!/bin/bash

if [ "$#" -ne 2 ];then
	echo "Usage: $0 <username> <password>"
	exit 1
fi

username=$1
password=$2


if ! id "$username" &> /dev/null; then
	echo "User $username does not exist."
	exit 1
fi



echo "$username:$password" | sudo chpasswd

if [ $? -eq 0 ];then
	echo "Password for user $username updated successfully."
else
	echo "Error updating password for user $username ."
fi
 





