<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstSubDistrict.home.createOrEditLabel"
          data-cy="MstSubDistrictCreateUpdateHeading"
          v-text="t$('monolithApp.mstSubDistrict.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstSubDistrict.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstSubDistrict.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstSubDistrict.name')" for="mst-sub-district-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="mst-sub-district-name"
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
            <label class="form-control-label" v-text="t$('monolithApp.mstSubDistrict.unm49Code')" for="mst-sub-district-unm49Code"></label>
            <input
              type="text"
              class="form-control"
              name="unm49Code"
              id="mst-sub-district-unm49Code"
              data-cy="unm49Code"
              :class="{ valid: !v$.unm49Code.$invalid, invalid: v$.unm49Code.$invalid }"
              v-model="v$.unm49Code.$model"
            />
            <div v-if="v$.unm49Code.$anyDirty && v$.unm49Code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.unm49Code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.mstSubDistrict.isoAlpha2Code')"
              for="mst-sub-district-isoAlpha2Code"
            ></label>
            <input
              type="text"
              class="form-control"
              name="isoAlpha2Code"
              id="mst-sub-district-isoAlpha2Code"
              data-cy="isoAlpha2Code"
              :class="{ valid: !v$.isoAlpha2Code.$invalid, invalid: v$.isoAlpha2Code.$invalid }"
              v-model="v$.isoAlpha2Code.$model"
            />
            <div v-if="v$.isoAlpha2Code.$anyDirty && v$.isoAlpha2Code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.isoAlpha2Code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstSubDistrict.district')" for="mst-sub-district-district"></label>
            <select
              class="form-control"
              id="mst-sub-district-district"
              data-cy="district"
              name="district"
              v-model="mstSubDistrict.district"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstSubDistrict.district && mstDistrictOption.id === mstSubDistrict.district.id
                    ? mstSubDistrict.district
                    : mstDistrictOption
                "
                v-for="mstDistrictOption in mstDistricts"
                :key="mstDistrictOption.id"
              >
                {{ mstDistrictOption.name }}
              </option>
            </select>
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
<script lang="ts" src="./mst-sub-district-update.component.ts"></script>
