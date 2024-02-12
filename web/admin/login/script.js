document.getElementById('attendance-form')
        .addEventListener('submit', function(event) {
    event.preventDefault();

    // collect form data
    const formData = new FormData(event.target);

    // convert form data into JSON
    const jsonObject = {};
    formData.forEach((value, key) => {
        jsonObject[key] = value;
    });

    // DEBUGGING
    console.log(jsonObject.name);

    // // send JSON data using Fetch API
    // fetch('http://localhost:8080/users', {
    //     method: 'GET',
    //     headers: {
    //         'Content-Type': 'Application/json',
    //     },
    //     body: JSON.stringify(jsonObject),
    // })
    // .then(response => {
    //
    // })
    // .then(data => {
    //     console.log(data);
    // })
    // .catch(error => {
    //
    // });

});
