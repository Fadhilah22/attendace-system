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
    .then(response => response.json())
    .then(data => {
        console.log(data);
    })
    .catch(error => {
        console.error('Error: ', error);
    });

});
