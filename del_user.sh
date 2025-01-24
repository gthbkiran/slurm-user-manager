#!/bin/bash

if [ "$#" -ne 1 ];then
	echo "Usage: $0 <username> "
	exit 1
fi

username=$1

if ! id "$username" &> /dev/null;then
	echo "User $username does not exist ."
	exit 1
fi

sudo userdel $username

if [ $? -eq 0 ];then
	echo "User $username deleted successfully. "
else
	echo "Error deleting user $username ."
fi




