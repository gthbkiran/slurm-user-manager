#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <username> <password>"
    exit 1
fi
username=$1
password=$2

# Check if the user already exists
if id "$username" &>/dev/null; then
    echo "User '$username' already exists."
    exit 1
fi

sudo useradd -m -s /bin/bash -p $(openssl passwd -1 $password) $username
if [ $? -eq 0 ]; then
sudo usermod -a -G sudo $username

sudo mkdir /home/$username/mydir
sudo chown -R $username:$username /home/$username/mydir
sudo usermod -d /home/$username/mydir $username

echo "$username ALL=(ALL) NOPASSWD:ALL" >> sudo /etc/sudoers

echo "User $username created successfully!"
echo "User $username added to sudo group!"
else
echo "Error while creating user!"
fi
