#!/bin/bash

# Get the list of usernames (UID >= 1000)
usernames=$(awk -F: '$3 >= 1000 {print $1}' /etc/passwd)

# Count the number of users
user_count=$(echo "$usernames" | wc -l)

# Output the number of users and list of usernames (one per line)
echo "Number of regular users: $user_count"
echo
echo "$usernames"




