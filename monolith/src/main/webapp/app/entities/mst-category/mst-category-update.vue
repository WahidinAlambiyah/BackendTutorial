<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstCategory.home.createOrEditLabel"
          data-cy="MstCategoryCreateUpdateHeading"
          v-text="t$('monolithApp.mstCategory.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstCategory.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstCategory.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstCategory.name')" for="mst-category-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="mst-category-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstCategory.description')" for="mst-category-description"></label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="mst-category-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./mst-category-update.component.ts"></script>
