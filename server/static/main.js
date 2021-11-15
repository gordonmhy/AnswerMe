const PORT = 5000;
const PROTOCOL = "http";

document.addEventListener('DOMContentLoaded', () => {
    let allQuestionsFrame = document.querySelector("#allQuestionsFrame");
    allQuestionsFrame.src = `${PROTOCOL}://${window.location.hostname}:${PORT}/all`;
    const updateFrame = () => {
        let cat = displayQuestionCategory.options[displayQuestionCategory.selectedIndex].value;
        allQuestionsFrame.src = `${PROTOCOL}://${window.location.hostname}:${PORT}/all${cat !== "all" ? "/" + cat : ""}`;
    }
    let displayQuestionCategory = document.querySelector("#displayQuestionCategory");
    displayQuestionCategory.addEventListener("change", updateFrame);
    let refreshButton = document.querySelector("#refreshButton");
    refreshButton.addEventListener("click", updateFrame)
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
        addQuestionFormStatus.style.color = "gray";
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
        fetch(`${PROTOCOL}://${window.location.hostname}:${PORT}/add`, {
            body: JSON.stringify(data),
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
        }).then(response => response.json()).then((response) => {
            addQuestionFormStatus.innerHTML = `Result: <br>${JSON.stringify(response, null, 4)}`;
            addQuestionFormStatus.style.color = response.success === undefined ? "darkred" : "darkgreen";
            questionLine1.value = "";
            questionLine2.value = "";
            questionNote.value = "";
            questionAnswer1.value = "";
            questionAnswer2.value = "";
            updateFrame();
        });
    });
    let deleteQuestionFormSubmitButton = document.querySelector("#deleteQuestionFormSubmit");
    deleteQuestionFormSubmitButton.addEventListener("click", (event) => {
        event.preventDefault();
        let questionId = document.querySelector("#questionId");
        let deleteQuestionFormStatus = document.querySelector("#deleteQuestionFormStatus");
        deleteQuestionFormStatus.innerHTML = "Loading, please wait...";
        deleteQuestionFormStatus.style.display = "block";
        deleteQuestionFormStatus.style.color = "gray";
        fetch(`${PROTOCOL}://${window.location.hostname}:${PORT}/del/${questionId.value}`, {
            method: "DELETE"
        }).then(
            response => response.json()
        ).then(response => {
            deleteQuestionFormStatus.innerHTML = `Result: <br>${JSON.stringify(response, null, 4)}`;
            deleteQuestionFormStatus.style.color = response.success === undefined ? "darkred" : "darkgreen";
            questionId.value = "";
            updateFrame();
        })
    })
})