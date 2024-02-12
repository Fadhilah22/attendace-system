document.getElementById('attendance-form')
        .addEventListener('submit', function(event) {
    event.preventDefault();

    // collect form data
    const formData = new FormData(event.target);

    // convert form data into JSON
    const jsonObject = {};
    formData.forEach((value, key) => {
        console.log(key);
        console.log(value);
        jsonObject[key] = value;
    });

    if(jsonObject.id == ''){
        alert("please provide an Id");
        reset();
    }

    console.log(jsonObject.id);

    // send JSON data using Fetch API
    fetch('http://localhost:8080/users', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'Application/json',
        },
        body: JSON.stringify(jsonObject),
    })
    .then(response => {
        if(response.status == 200){
            return response.json();
        } else if(response.status == 404){
            throw new Error('id not found.');
        } else {
            throw new Error('failed to delete user.');
        }
    })
    .then(data => {
        console.log(data);
    })
    .catch(error => {
        if(!(error instanceof SyntaxError)){
            alert(error);
        } else {
            alert("Data deleted!");
            document.getElementById('attendance-form').reset();
        }
    });

});
