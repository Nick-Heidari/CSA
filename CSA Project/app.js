const API = "http://localhost:8000/api";

let deletedCount = parseInt(localStorage.getItem("deletedCount") || "0");

function showSection(id) {
  document.querySelectorAll(".section").forEach(s => {
    s.classList.remove("active");
    s.classList.add("hidden");
  });
  document.querySelectorAll(".nav-btn").forEach(b => b.classList.remove("active"));

  const section = document.getElementById(id);
  section.classList.remove("hidden");
  section.classList.add("active");

  document.querySelectorAll(".nav-btn").forEach(btn => {
    if (btn.getAttribute("onclick").includes(id)) {
      btn.classList.add("active");
    }
  });

  if (id === "recipes") loadPantryRecipes();
}

function loadItems() {
  fetch(API + "/items")
    .then(res => res.json())
    .then(items => {
      const list = document.getElementById("pantry-list");
      const badge = document.getElementById("item-count");
      badge.textContent = items.length + (items.length === 1 ? " item" : " items");

      if (items.length === 0) {
        list.innerHTML = '<li class="empty-state">Your pantry is empty. Add some items! 🛒</li>';
        return;
      }

      list.innerHTML = "";
      items.forEach(item => {
        const li = document.createElement("li");
        const daysLeft = getDaysUntilExpiry(item.expiryDate);
        const dateClass = daysLeft <= 3 ? "item-date expiring-soon" : "item-date";
        const dateLabel = formatDateLabel(item.expiryDate, daysLeft);

        li.innerHTML = `
          <div class="item-info">
            <span class="item-name">${escapeHtml(item.name)}</span>
            <span class="${dateClass}">${dateLabel}</span>
          </div>
          <button class="btn-delete" onclick="deleteItem(${item.id})">Used ✓</button>
        `;
        list.appendChild(li);
      });
    })
    .catch(() => {
      document.getElementById("pantry-list").innerHTML =
        '<li class="empty-state">⚠️ Could not connect to backend. Is it running at localhost:8000?</li>';
    });
}

function addItem() {
  const name = document.getElementById("item-name").value.trim();
  const date = document.getElementById("item-date").value;
  const feedback = document.getElementById("add-feedback");

  if (!name || !date) {
    showFeedback(feedback, "Please fill in both the name and expiry date.", true);
    return;
  }

  fetch(API + "/items", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, expiryDate: date })
  })
    .then(res => {
      if (!res.ok) throw new Error("Failed to add item");
      return res.json();
    })
    .then(() => {
      document.getElementById("item-name").value = "";
      document.getElementById("item-date").value = "";
      showFeedback(feedback, `"${name}" added to your pantry! ✓`, false);
      loadItems();
    })
    .catch(() => {
      showFeedback(feedback, "Error adding item. Is the backend running?", true);
    });
}

function deleteItem(id) {
  fetch(API + "/delete?id=" + id)
    .then(() => {
      deletedCount++;
      updateImpact();
      loadItems();
      checkExpiring();
    })
    .catch(() => alert("Error deleting item."));
}

function checkExpiring() {
  fetch(API + "/expiring")
    .then(res => res.json())
    .then(items => {
      const banner = document.getElementById("alert-banner");
      if (items.length > 0) {
        banner.textContent = `${items.length} item${items.length > 1 ? "s" : ""} expiring within 3 days!`;
        banner.classList.remove("hidden");
      } else {
        banner.classList.add("hidden");
      }
    })
    .catch(() => {});
}

// ─── RECIPE SECTION ───────────────────────────────────────────

let pantryIngredients = [];

function loadPantryRecipes() {
  fetch(API + "/items")
    .then(res => res.json())
    .then(items => {
      pantryIngredients = items.map(i => i.name);
      const tagContainer = document.getElementById("pantry-tags");
      tagContainer.innerHTML = "";

      // ── Use It Up Banner ──────────────────────────────────────
      const banner = document.getElementById("use-it-up-banner");
      const bannerList = document.getElementById("use-it-up-list");

      const expiringSoon = items
        .map(item => ({ ...item, daysLeft: getDaysUntilExpiry(item.expiryDate) }))
        .filter(item => item.daysLeft <= 5)
        .sort((a, b) => a.daysLeft - b.daysLeft)
        .slice(0, 3);

      if (expiringSoon.length > 0) {
        bannerList.innerHTML = "";
        expiringSoon.forEach(item => {
          const chip = document.createElement("button");
          chip.className = "use-it-up-chip";
          const label = item.daysLeft <= 0 ? "expired!" : item.daysLeft === 1 ? "1 day left" : item.daysLeft + " days left";
          chip.innerHTML = escapeHtml(item.name) + " <em>" + label + "</em>";
          chip.onclick = () => {
            document.querySelectorAll(".ingredient-tag").forEach(t => t.classList.remove("active"));
            const match = document.querySelector(".ingredient-tag[data-ingredient='" + item.name.replace(/'/g, "\\'") + "']");
            if (match) match.classList.add("active");
            fetchRecipesForAll([item.name]);
            document.getElementById("recipe-results").scrollIntoView({ behavior: "smooth" });
          };
          bannerList.appendChild(chip);
        });
        banner.classList.remove("hidden");
      } else {
        banner.classList.add("hidden");
      }
      // ─────────────────────────────────────────────────────────

      if (items.length === 0) {
        tagContainer.innerHTML = '<span class="no-items-hint">No pantry items yet — add some first!</span>';
        return;
      }

      items.forEach(item => {
        const tag = document.createElement("button");
        tag.className = "ingredient-tag active";
        tag.textContent = item.name;
        tag.dataset.ingredient = item.name;
        tag.onclick = () => {
          tag.classList.toggle("active");
          searchFromPantry();
        };
        tagContainer.appendChild(tag);
      });

      searchFromPantry();
    });
}

function searchFromPantry() {
  const activeTags = document.querySelectorAll(".ingredient-tag.active");
  if (activeTags.length === 0) {
    document.getElementById("recipe-results").innerHTML =
      '<li class="recipe-empty">Select at least one ingredient above.</li>';
    return;
  }
  const ingredients = Array.from(activeTags).map(t => t.dataset.ingredient);
  fetchRecipesForAll(ingredients);
}

function searchRecipes() {
  const ingredient = document.getElementById("recipe-search-input").value.trim();
  if (!ingredient) return;
  document.querySelectorAll(".ingredient-tag").forEach(t => t.classList.remove("active"));
  fetchRecipesForAll([ingredient]);
}

function fetchRecipesForAll(ingredients) {
  const results = document.getElementById("recipe-results");
  results.innerHTML = '<li class="recipe-loading"><div class="spinner"></div> Finding recipes...</li>';

  // Fetch for each ingredient in parallel, then merge & deduplicate
  const fetches = ingredients.map(ing =>
    fetch("https://www.themealdb.com/api/json/v1/1/filter.php?i=" + encodeURIComponent(ing))
      .then(res => res.json())
      .then(data => data.meals || [])
      .catch(() => [])
  );

  Promise.all(fetches).then(arrays => {
    const seen = new Set();
    const allMeals = [];
    arrays.forEach(meals => {
      meals.forEach(meal => {
        if (!seen.has(meal.idMeal)) {
          seen.add(meal.idMeal);
          allMeals.push(meal);
        }
      });
    });

    if (allMeals.length === 0) {
      results.innerHTML = '<li class="recipe-empty">No recipes found for those ingredients. Try selecting different ones.</li>';
      return;
    }

    results.innerHTML = "";
    allMeals.forEach(meal => {
      const li = document.createElement("li");
      li.className = "recipe-card";
      li.innerHTML = `
        <div class="recipe-img-wrap">
          <img src="${meal.strMealThumb}/preview" alt="${escapeHtml(meal.strMeal)}" loading="lazy" />
          <div class="recipe-overlay">
            <span class="recipe-view-btn">View Recipe →</span>
          </div>
        </div>
        <div class="recipe-card-body">
          <div class="recipe-card-name">${escapeHtml(meal.strMeal)}</div>
        </div>
      `;
      li.onclick = () => openRecipeModal(meal.idMeal, meal.strMeal, meal.strMealThumb);
      results.appendChild(li);
    });
  });
}

function openRecipeModal(id, name, thumb) {
  const modal = document.getElementById("recipe-modal");
  const modalBody = document.getElementById("modal-body");

  modal.classList.remove("hidden");
  document.body.style.overflow = "hidden";

  modalBody.innerHTML = `
    <div class="modal-loading"><div class="spinner"></div> Loading recipe...</div>
  `;

  fetch(`https://www.themealdb.com/api/json/v1/1/lookup.php?i=${id}`)
    .then(res => res.json())
    .then(data => {
      const meal = data.meals[0];

      // Build ingredients list
      const ingredients = [];
      for (let i = 1; i <= 20; i++) {
        const ing = meal[`strIngredient${i}`];
        const measure = meal[`strMeasure${i}`];
        if (ing && ing.trim()) {
          ingredients.push(`${measure ? measure.trim() + " " : ""}${ing.trim()}`);
        }
      }

      // Split instructions into steps
      const steps = meal.strInstructions
        .split(/\r\n|\n|\r/)
        .map(s => s.trim())
        .filter(s => s.length > 10);

      modalBody.innerHTML = `
        <div class="modal-hero">
          <img src="${meal.strMealThumb}" alt="${escapeHtml(meal.strMeal)}" />
          <div class="modal-hero-overlay">
            <div class="modal-category">${meal.strCategory} · ${meal.strArea}</div>
            <h2 class="modal-title">${escapeHtml(meal.strMeal)}</h2>
          </div>
        </div>
        <div class="modal-content-grid">
          <div class="modal-ingredients">
            <h3>Ingredients</h3>
            <ul class="ingredients-list">
              ${ingredients.map(ing => `<li>${escapeHtml(ing)}</li>`).join("")}
            </ul>
          </div>
          <div class="modal-instructions">
            <h3>Instructions</h3>
            <ol class="steps-list">
              ${steps.map(step => `<li>${escapeHtml(step)}</li>`).join("")}
            </ol>
          </div>
        </div>
        ${meal.strYoutube ? `
          <div class="modal-youtube">
            <a href="${meal.strYoutube}" target="_blank" class="youtube-btn">▶ Watch on YouTube</a>
          </div>
        ` : ""}
      `;
    })
    .catch(() => {
      modalBody.innerHTML = `<div class="modal-loading">⚠️ Could not load recipe details.</div>`;
    });
}

function closeRecipeModal() {
  document.getElementById("recipe-modal").classList.add("hidden");
  document.body.style.overflow = "";
}

document.getElementById("recipe-modal").addEventListener("click", function(e) {
  if (e.target === this) closeRecipeModal();
});

document.addEventListener("keydown", e => {
  if (e.key === "Escape") closeRecipeModal();
});

// ─── SEARCH INPUT LISTENERS ───────────────────────────────────

document.getElementById("recipe-search-input").addEventListener("keydown", e => {
  if (e.key === "Enter") searchRecipes();
});

document.getElementById("item-name").addEventListener("keydown", e => {
  if (e.key === "Enter") document.getElementById("item-date").focus();
});
document.getElementById("item-date").addEventListener("keydown", e => {
  if (e.key === "Enter") addItem();
});

// ─── HELPERS ──────────────────────────────────────────────────

function updateImpact() {
  localStorage.setItem("deletedCount", deletedCount);
  document.getElementById("items-used").textContent = deletedCount;
  document.getElementById("food-saved").textContent = (deletedCount * 0.5).toFixed(1) + " lbs";
  document.getElementById("co2-avoided").textContent = (deletedCount * 1.25).toFixed(1) + " lbs";
}

function getDaysUntilExpiry(dateStr) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const expiry = new Date(dateStr + "T00:00:00");
  return Math.round((expiry - today) / (1000 * 60 * 60 * 24));
}

function formatDateLabel(dateStr, daysLeft) {
  const formatted = new Date(dateStr + "T00:00:00").toLocaleDateString("en-US", {
    month: "short", day: "numeric", year: "numeric"
  });
  if (daysLeft < 0) return `Expired ${Math.abs(daysLeft)}d ago`;
  if (daysLeft === 0) return "Expires today!";
  if (daysLeft === 1) return "Expires tomorrow";
  if (daysLeft <= 3) return `Expires in ${daysLeft} days (${formatted})`;
  return `Expires ${formatted}`;
}

function escapeHtml(str) {
  return String(str).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

function showFeedback(el, msg, isError) {
  el.textContent = msg;
  el.className = "feedback" + (isError ? " error" : "");
  setTimeout(() => { el.className = "feedback hidden"; }, 4000);
}

loadItems();
checkExpiring();
updateImpact();
