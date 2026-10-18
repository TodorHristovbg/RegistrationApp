
const form = document.getElementById("form")
form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(form);
    const email = formData.get("email");
    const password = formData.get("password");
    const captcha = formData.get("captcha");

    if(!email || !password){
        alert("Please fill in all fields");
        return;
    }
    if(!captcha){
        alert("Please fill in captcha");
        return;
    } 
    
    const body = new URLSearchParams(formData);
    try{
         const response = await fetch("verifycaptcha", {
           method: "POST",
           headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
           body: body
        });

        if(!response.ok){
            const message = await response.text();
            alert(message);
            return;
        }
    }
    catch (error){
        console.error("Fetch failed:", error);
        alert("Request failed: " + error.message);
    }
    try{
        const response = await fetch(form.action, {
           method: "POST",
           headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
           body: body
        });

        if(response.ok){
            const message = await response.text();
            alert(message);
            window.location.href="Profile.html";
        } else {
            const message = await response.text();
            alert(message);
        }
    } catch (error){
        console.error("Fetch failed:", error);
        alert("Request failed: " + error.message);
    }

});