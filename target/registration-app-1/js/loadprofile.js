
document.addEventListener("DOMContentLoaded", loadprofile);

async function loadprofile() {
    try {
        const response = await fetch("loadprofile");

        console.log("Status:", response.status);

        if (response.status === 401) {
            alert("Login first");
            window.location.href = "Login.html"; 
            return;
        }

        if (!response.ok) {
            const message = await response.text();
            alert(message || "Failed to load profile");
            return;
        }

        const data = await response.json();

        console.log("Profile:", data);

        document.getElementById("first_name").value = data.first_name;

        document.getElementById("second_name").value = data.second_name;

        document.getElementById("email").value = "Email: " + data.email;

    } catch (error) {
        console.error("Fetch failed:", error);
        alert("Load profile failed: " + error.message);
    }
}
async function changefirstname() {
   const new_first_name = document.getElementById("first_name").value.trim();
   if(!new_first_name){
    alert("Please enter a name before attempting to change");
    return;
   } else {
    alert(new_first_name);
   }

    try{
        const formData = new URLSearchParams();
        formData.append("new_first_name", new_first_name);

        const response = await fetch("updatefirstname", {
           method: "POST",
           headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
           body: formData
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
}

async function changesecondname() {
   const new_second_name = document.getElementById("second_name").value.trim();
   if(!new_second_name){
    alert("Please enter a name before attempting to change");
     return;
   } else {
    alert(new_second_name);
   }

    try{
        const formData = new URLSearchParams();
        formData.append("new_second_name", new_second_name);

        const response = await fetch("updatesecondname", {
           method: "POST",
           headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
           body: formData
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
}

async function changepassword() {
   const new_password = document.getElementById("password").value.trim();
   const new_password2 = document.getElementById("password2").value.trim();
   if(!new_password){
    alert("Please enter a password before attempting to change");
     return;
   } else if(new_password!=new_password2){
    alert("Passwords must match");
     return;
   }
   else {
    alert(new_password);
   }

    try{
        const formData = new URLSearchParams();
        formData.append("new_password", new_password);

        const response = await fetch("updatepassword", {
           method: "POST",
           headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
           body: formData
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
}

async function logout(){
    alert("attempting log out");
    const response = await fetch("logout", {
           method: "POST",
    });
    if(response.ok){
        window.location.href="index.html";
    } else {
        alert("Logout failed" + await response.text())
    }
}