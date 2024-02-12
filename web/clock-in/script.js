document.getElementById('attendance-form')
        .addEventListener('submit', function(event) {
    event.preventDefault();

    // collect form data
    const formData = new FormData(event.target);

    // convert form data into JSON
    const jsonObject = {};
    formData.forEach((value, key) => {
        // console.log(key);
        // console.log(value)
        jsonObject[key] = value;
    });

    // DEBUGGING
    console.log(jsonObject.id);
    console.log(jsonObject.name);
    console.log(jsonObject.email);

    // send JSON data using Fetch API
    fetch('http://localhost:8080/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'Application/json',
        },
        body: JSON.stringify(jsonObject),
    })
    .then(response => {
        if(response.status == 200){
            // RESPONSE OK
            return response.json();
        } else if (response.status == 409){
            // DATA ALREADY EXISTS
            throw new Error("DuplicateError");
        }
    })
    .then(data => {
        console.log(data);
    })
    .catch(error => {
        if(error instanceof SyntaxError){
            alert("Data added");
            document.getElementById('attendance-form').reset();
        } else if (error.message == "DuplicateError") {
            console.error('', error);
            alert("data with similiar id exists");
        } else {
            console.error('', error);
            alert("oops, server error");
        }
    });

});
