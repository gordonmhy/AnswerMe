document.addEventListener('DOMContentLoaded', () => {
    let addQuestionFormSubmitButton = document.querySelector("#addQuestionFormSubmit");
    addQuestionFormSubmitButton.addEventListener("click", (event) => {
        event.preventDefault();
        let category = document.querySelector("select");
        let questionLine1 = document.querySelector("#questionLine1");
        let questionLine2 = document.querySelector("#questionLine2");
        let questionNote = document.querySelector("#questionNote");
        let questionAnswer1 = document.querySelector("#questionAnswer1");
        let questionAnswer2 = document.querySelector("#questionAnswer2");
        let addQuestionFormStatus = document.querySelector("#addQuestionFormStatus");
        addQuestionFormStatus.innerHTML = "Loading, please wait...";
        addQuestionFormStatus.style.display = "block";
        addQuestionFormStatus.style.color = "black";
        let data = {
            'cat': category.options[category.selectedIndex].value,
            'ql1': questionLine1.value,
            'note': questionNote.value,
            'ans1': questionAnswer1.value,
        }
        if (questionLine2.value !== '') {
            data.ql2 = questionLine2.value
        }
        if (questionAnswer2.value !== '') {
            data.ans2 = questionAnswer2.value
        }
        fetch(`http://${window.location.hostname}/add`, {
            method: "POST",
            body: JSON.stringify(data),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => response.json()).then((response) => {
            addQuestionFormStatus.innerHTML = `Result: <br>${JSON.stringify(response)}`;
            addQuestionFormStatus.style.color = response.success === undefined ? "darkred" : "darkgreen";
        })
    })
})