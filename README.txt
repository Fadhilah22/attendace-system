GETALL REQUEST -> shows userid, name and clock-in date

POSTUSER REQUEST -> post username, useremail, and user clock-in date (Automatic). user data must be in Json format.
{
  "name" : "User-name",
  "email" : "User.Email@mail.com"
}

DELETEUSER REQUEST -> deletes user data given user id
{
  "id" : "a6ad9d38-284e-436f-a11f-d22d193ae45b" #id is 32bit alphanumeric in this case
}
